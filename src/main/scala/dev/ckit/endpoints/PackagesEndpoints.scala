package dev.ckit.endpoints

import io.github.edadma.apion.*
import io.github.edadma.rdb.{DB, executeQuery, QueryResult}
import dev.ckit.models.*

import scala.concurrent.Future

import pprint.pprintln

object PackagesEndpoints {

  def listPackages(request: Request)(implicit db: DB): Future[Result] = {
    try {
      // Parse query parameters
      val queryParams = request.query
      val searchQuery = queryParams.get("q")
      val sortBy      = queryParams.getOrElse("sort", "name")
      val page        = queryParams.get("page").flatMap(_.toIntOption).getOrElse(1)
      val pageSize    = queryParams.get("page_size").flatMap(_.toIntOption).getOrElse(20)

      // Validate parameters
      if (page < 1) {
        return ApiError(
          error = "invalid_parameter",
          message = "Page must be greater than 0",
        ).asJson(400)
      }

      if (pageSize < 1 || pageSize > 100) {
        return ApiError(
          error = "invalid_parameter",
          message = "Page size must be between 1 and 100",
        ).asJson(400)
      }

      // Build SQL query
      val whereClause = searchQuery match {
        case Some(q) => s"WHERE (p.name LIKE '%$q%' OR p.description LIKE '%$q%')"
        case None    => ""
      }

      val orderClause = sortBy match {
        case "downloads" => "ORDER BY p.downloads DESC, p.name ASC"
        case "name"      => "ORDER BY p.name ASC"
        case _           => "ORDER BY p.name ASC" // default to name
      }

      val offset = (page - 1) * pageSize

      pprintln(executeQuery(s"select * from packages p $whereClause"))
      // Count total packages
      val QueryResult(totalCount) = executeQuery(s"SELECT COUNT(*) AS count FROM packages p $whereClause")
      pprintln(totalCount)
      println("----------------")
      pprintln(executeQuery(s"SELECT COUNT(p.name) AS count FROM packages p"))
      pprintln(executeQuery(s"SELECT COUNT(*) AS count FROM packages p"))
      println(("whereClause", whereClause))

      // Get packages with pagination
      val packagesSql = s"""
        SELECT p.id, p.name, p.display_name, p.description, p.license, p.downloads, p.weekly_downloads
        FROM packages p
        $whereClause
        $orderClause
        LIMIT $pageSize OFFSET $offset
      """

      val QueryResult(packageRows) = executeQuery(packagesSql)

      val packages = packageRows.data.map { row =>
        Package(
          id = row.getString("id"),
          name = row.getString("name"),
          displayName = row.getStringOption("display_name"),
          description = row.getString("description"),
          license = row.getString("license"),
          downloads = row.getLong("downloads"),
          weeklyDownloads = row.getLong("weekly_downloads"),
        )
      }

      PackageListResponse(
        packages = packages,
        totalCount = totalCount.data.head.getInt("count"),
        page = page,
        pageSize = pageSize,
      ).asJson

    } catch {
      case e: Exception =>
        println(s"Error processing packages request: ${e.getMessage}")
        e.printStackTrace()
        ApiError(
          error = "internal_error",
          message = s"Error processing request: ${e.getMessage}",
        ).asJson(500)
    }
  }

  def getPackage(request: Request)(implicit db: DB): Future[Result] = {
    try {
      val packageId = request.params("id")

      // Validate UUID format
      if (!isValidUUID(packageId)) {
        return ApiError(
          error = "invalid_package_id",
          message = "Package ID must be a valid UUID",
        ).asJson(400)
      }

      // Get package info with owner - USING NEW RDB API
      val packageSql = s"""
        SELECT p.id, p.name, p.display_name, p.description, p.git_url, p.homepage, p.documentation,
               p.license, p.metadata, p.downloads, p.weekly_downloads,
               p.is_deprecated, p.deprecation_message, p.created_at, p.updated_at,
               u.username as owner_username
        FROM packages p
        JOIN users u ON p.owner_id = u.id
        WHERE p.id = '$packageId'
      """

      val QueryResult(packageRows) = executeQuery(packageSql)

      packageRows.data.headOption match {
        case Some(row) =>
          // Parse metadata JSON
          val metadata = parseMetadata(row.getString("metadata"))

          // Get all versions using package ID
          val versionsSql = s"""
            SELECT pv.id, pv.version, pv.git_tag, pv.git_commit, pv.description,
                   pv.is_prerelease, pv.is_yanked, pv.yank_reason, pv.published_at,
                   u.username as published_by
            FROM package_versions pv
            JOIN users u ON pv.published_by = u.id
            WHERE pv.package_id = '$packageId'
            ORDER BY pv.is_yanked ASC, pv.published_at DESC
          """

          val QueryResult(versionRows) = executeQuery(versionsSql)
          val versions                 = versionRows.data.map(_.getString("version"))
          val latestVersion            = versions.headOption.getOrElse("0.0.0")

          // Get latest version details
          val latestVersionInfo = versionRows.data.headOption.map { vrow =>
            PackageVersion(
              id = vrow.getString("id"),
              packageId = packageId,
              version = vrow.getString("version"),
              gitTag = vrow.getString("git_tag"),
              gitCommit = vrow.getString("git_commit"),
              description = vrow.getStringOption("description"),
              isPrerelease = vrow.getBoolean("is_prerelease"),
              isYanked = vrow.getBoolean("is_yanked"),
              yankReason = vrow.getStringOption("yank_reason"),
              publishedAt = vrow.getString("published_at"),
              publishedBy = vrow.getString("published_by"),
            )
          }

          // Get maintainers using package ID
          val maintainersSql =
            s"""
              SELECT u.username
              FROM package_ownerships po
              JOIN users u ON po.user_id = u.id
              WHERE po.package_id = '$packageId'
                AND po.role IN ('owner', 'maintainer')
              ORDER BY
                CASE po.role WHEN 'owner' THEN 1 WHEN 'maintainer' THEN 2 END,
                u.username ASC
            """
          val QueryResult(maintainerRows) = executeQuery(maintainersSql)
          val maintainers                 = maintainerRows.data.map(_.getString("username"))

          // Build response
          PackageDetail(
            id = row.getString("id"),
            name = row.getString("name"),
            displayName = row.getStringOption("display_name"),
            description = row.getString("description"),
            gitUrl = row.getString("git_url"),
            homepage = row.getStringOption("homepage"),
            documentation = row.getStringOption("documentation"),
            license = row.getString("license"),
            keywords = metadata.keywords,
            categories = metadata.categories,
            platforms = metadata.platforms,
            downloads = row.getLong("downloads"),
            weeklyDownloads = row.getLong("weekly_downloads"),
            isDeprecated = row.getBoolean("is_deprecated"),
            deprecationMessage = row.getStringOption("deprecation_message"),
            ownerUsername = row.getString("owner_username"),
            maintainers = maintainers,
            versions = versions,
            latestVersion = latestVersion,
            latestVersionInfo = latestVersionInfo,
            createdAt = row.getString("created_at"),
            updatedAt = row.getString("updated_at"),
          ).asJson

        case None =>
          ApiError(
            error = "package_not_found",
            message = s"Package with ID '$packageId' not found",
          ).asJson(404)
      }

    } catch {
      case e: Exception =>
        println(s"Error getting package details: ${e.getMessage}")
        e.printStackTrace()
        ApiError(
          error = "internal_error",
          message = s"Error processing request: ${e.getMessage}",
        ).asJson(500)
    }
  }

  /** Validate UUID format
    */
  private def isValidUUID(uuid: String): Boolean = {
    try {
      java.util.UUID.fromString(uuid)
      true
    } catch {
      case _: IllegalArgumentException => false
    }
  }

  /** Parse package metadata JSON
    */
  private case class PackageMetadata(
      keywords: List[String] = List.empty,
      categories: List[String] = List.empty,
      platforms: List[String] = List.empty,
  )

  private def parseMetadata(metadataJson: String): PackageMetadata = {
    try {
      // Simple JSON parsing - extract arrays from JSON string
      PackageMetadata(
        keywords = extractJsonArray(metadataJson, "keywords"),
        categories = extractJsonArray(metadataJson, "categories"),
        platforms = extractJsonArray(metadataJson, "platforms"),
      )
    } catch {
      case e: Exception =>
        println(s"Warning: Failed to parse metadata JSON: $metadataJson")
        PackageMetadata()
    }
  }

  private def extractJsonArray(json: String, key: String): List[String] = {
    try {
      val pattern = s""""$key"\\s*:\\s*\\[(.*?)\\]""".r
      pattern.findFirstMatchIn(json).map(_.group(1)) match {
        case Some(arrayContent) =>
          arrayContent.split(",")
            .map(_.trim.stripPrefix("\"").stripSuffix("\""))
            .filter(_.nonEmpty)
            .toList
        case None => List.empty
      }
    } catch {
      case e: Exception =>
        List.empty
    }
  }

  def createPackage(request: Request)(implicit db: DB): Future[Result] = {
    // TODO: Implement POST /api/v1/packages (requires auth)
    ApiError("not_implemented", "Create package endpoint not yet implemented").asJson(501)
  }
}
