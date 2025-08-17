package dev.ckit

import io.github.edadma.apion.*
import io.github.edadma.rdb.{DB, MemoryDB, executeSQL}
import io.github.edadma.cross_platform.readFile
import zio.json.*

import java.time.Instant

case class Health(status: String, timestamp: Instant) derives JsonEncoder

def createKitServer: Server =
  implicit val db: DB = new MemoryDB

// Initialize schema
  initializeSchema()

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
