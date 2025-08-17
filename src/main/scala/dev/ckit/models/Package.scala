package dev.ckit.models

import zio.json.*

case class Package(
    name: String,
    displayName: Option[String],
    description: String,
    license: String,
    downloads: Long,
    weeklyDownloads: Long,
) derives JsonEncoder

case class PackageListResponse(
    packages: List[Package],
    totalCount: Int,
    page: Int,
    pageSize: Int,
) derives JsonEncoder

case class ApiError(
    error: String,
    message: String,
    timestamp: String = java.time.Instant.now().toString,
) derives JsonEncoder
