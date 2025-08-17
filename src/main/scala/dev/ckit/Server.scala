package dev.ckit

import dev.ckit.routes.PackagesRoutes
import io.github.edadma.apion.*
import io.github.edadma.rdb.{DB, MemoryDB, executeSQL}
import io.github.edadma.cross_platform.readFile
import zio.json.*

import java.time.Instant

case class Health(status: String, timestamp: Instant) derives JsonEncoder

case class PackageSearchEcho(
    queryParameters: Map[String, String],
    message: String,
) derives JsonEncoder

def createKitServer: Server =
  implicit val db: DB = new MemoryDB

  // Initialize database
  initializeDatabase()

  // Create base server with middleware and health check
  var server = Server()
    .use(LoggingMiddleware())
    .use(CorsMiddleware())
    .use("/", StaticMiddleware("static"))
    .get(
      "/health",
      _ => {
        Health(
          status = "healthy",
          timestamp = Instant.now,
        ).asJson
      },
    )

  // Add routes for each resource
  server = PackagesRoutes.addRoutes(server)

  // Future: Add more resource routes
  // server = UsersRoutes.addRoutes(server)
  // server = AuthRoutes.addRoutes(server)

  server

private def initializeDatabase()(implicit db: DB): Unit = {
  try {
    // Initialize schema
    val schemaSql = readFile("schema.sql")
    executeSQL(schemaSql)
    println("✅ Database schema initialized successfully")

    // Load seed data
    val dataSql = readFile("data.sql")
    executeSQL(dataSql)
    println("✅ Seed data loaded successfully")

  } catch {
    case e: Exception =>
      println(s"❌ Failed to initialize database: ${e.getMessage}")
      throw e
  }
}
