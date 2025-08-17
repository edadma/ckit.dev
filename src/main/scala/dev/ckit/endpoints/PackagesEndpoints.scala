package dev.ckit.endpoints

import io.github.edadma.apion.*
import io.github.edadma.rdb.{DB, executeQuery, QueryResult}
import dev.ckit.models.*
import zio.json.*

import scala.concurrent.Future

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

      // Count total packages
      val QueryResult(totalCount) = executeQuery(s"SELECT COUNT(*) AS count FROM packages p $whereClause")

      // Get packages with pagination
      val packagesSql = s"""
        SELECT p.name, p.display_name, p.description, p.license, p.downloads, p.weekly_downloads
        FROM packages p
        $whereClause
        $orderClause
        LIMIT $pageSize OFFSET $offset
      """

      val QueryResult(packageRows) = executeQuery(packagesSql)

      val packages = packageRows.data.map { row =>
        Package(
          name = row.getString("name"),
          displayName = row.getStringOption("displayName"),
          description = row.getString("description"),
          license = row.getString("license"),
          downloads = row.getLong("downloads"),
          weeklyDownloads = row.getLong("weeklyDownloads"),
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

  // Future endpoints for packages resource
  def getPackage(request: Request)(implicit db: DB): Future[Result] = {
    // TODO: Implement GET /api/v1/packages/:name
    ApiError("not_implemented", "Get package endpoint not yet implemented").asJson(501)
  }

  def createPackage(request: Request)(implicit db: DB): Future[Result] = {
    // TODO: Implement POST /api/v1/packages (requires auth)
    ApiError("not_implemented", "Create package endpoint not yet implemented").asJson(501)
  }
}
