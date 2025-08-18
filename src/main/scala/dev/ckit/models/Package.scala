package dev.ckit.models

import zio.json.*

case class Package(
    id: String,
    name: String,
    displayName: Option[String],
    description: String,
    license: String,
    downloads: Long,
    weeklyDownloads: Long,
) derives JsonEncoder

case class PackageListResponse(
    packages: Seq[Package],
    totalCount: Int,
    page: Int,
    pageSize: Int,
) derives JsonEncoder

case class ApiError(
    error: String,
    message: String,
    timestamp: String = java.time.Instant.now().toString,
) derives JsonEncoder

case class PackageVersion(
    id: String,
    packageId: String,
    version: String,
    gitTag: String,
    gitCommit: String,
    description: Option[String],
    isPrerelease: Boolean,
    isYanked: Boolean,
    yankReason: Option[String],
    publishedAt: String,
    publishedBy: String,
) derives JsonEncoder

case class PackageDetail(
    id: String,
    name: String,
    displayName: Option[String],
    description: String,
    gitUrl: String,
    homepage: Option[String],
    documentation: Option[String],
    license: String,
    keywords: List[String],
    categories: List[String],
    platforms: List[String],
    downloads: Long,
    weeklyDownloads: Long,
    isDeprecated: Boolean,
    deprecationMessage: Option[String],
    ownerUsername: String,
    maintainers: Seq[String],
    versions: Seq[String],
    latestVersion: String,
    latestVersionInfo: Option[PackageVersion],
    createdAt: String,
    updatedAt: String,
) derives JsonEncoder
