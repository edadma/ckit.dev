package dev.ckit

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

// Initialize schema
  initializeSchema()

  val data = readFile("data.sql")
  executeSQL(data)

  Server()
    .use(LoggingMiddleware())
    .use(CorsMiddleware())
    .use("/", StaticMiddleware("static"))
    .get(
      "/health",
      _ =>
        val dbStatus  = executeSQL("SELECT 1").nonEmpty
        val timestamp = Instant.now()
        val status    = if (dbStatus) "healthy" else "unhealthy"

        Health(status, timestamp).asJson,
    )
    .get(
      "/api/v1/packages",
      request => {
        try {
          // Get all query parameters
          val queryParams = request.query

          // Echo them back with a message
          PackageSearchEcho(
            queryParameters = queryParams,
            message = s"Received ${queryParams.size} query parameters",
          ).asJson

        } catch {
          case e: Exception =>
            Map(
              "error"   -> "internal_error",
              "message" -> s"Error processing request: ${e.getMessage}",
            ).asJson(500)
        }
      },
    )
private def initializeSchema()(implicit db: DB): Unit =
  try {
    val schemaSql = readFile("schema.sql")

    executeSQL(schemaSql)
    println("✅ Database schema initialized successfully from schema.sql")
  } catch {
    case e: Exception =>
      println(s"❌ Failed to initialize database schema: ${e.getMessage}")
      throw e
  }
