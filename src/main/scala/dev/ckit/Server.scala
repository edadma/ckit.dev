package dev.ckit

import io.github.edadma.apion.*
import zio.json.*

def createKitServer: Server =
  Server()
    .use("/", StaticMiddleware("static"))
    .use(LoggingMiddleware())
    .use(CorsMiddleware())
