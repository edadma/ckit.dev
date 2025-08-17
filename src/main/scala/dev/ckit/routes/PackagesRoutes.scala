package dev.ckit.routes

import io.github.edadma.apion.*
import io.github.edadma.rdb.DB
import dev.ckit.endpoints.PackagesEndpoints

object PackagesRoutes {

  def addRoutes(server: Server)(implicit db: DB): Server = {
    server
      // GET /api/v1/packages - List/search packages
      .get("/api/v1/packages", PackagesEndpoints.listPackages)

    // Future routes for packages resource:
    // .get("/api/v1/packages/:name", PackagesEndpoints.getPackage)
    // .post("/api/v1/packages", PackagesEndpoints.createPackage)
    // .get("/api/v1/packages/:name/versions", PackagesEndpoints.getPackageVersions)
    // .get("/api/v1/packages/:name/versions/:version", PackagesEndpoints.getPackageVersion)
  }
}
