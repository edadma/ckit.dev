package dev.ckit

import io.github.edadma.apion.*
import zio.json.*

import scala.util.Try

def createKitServer: Server =
  Server()
    .use("/", StaticMiddleware("static"))
    .use(LoggingMiddleware())
    .use(CorsMiddleware())
    .get(
      "/health",
      _ =>
        for {
          dbStatus <- Try(executeSQL("SELECT 1")).isSuccess
          timestamp = Instant.now()
        } yield {
          val status = if (dbStatus) "healthy" else "unhealthy"
          Map("status" -> status, "timestamp" -> timestamp, "database" -> "rdb").asJson
        },
    )
