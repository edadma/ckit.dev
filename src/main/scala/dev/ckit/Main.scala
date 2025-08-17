package dev.ckit

@main
def run(): Unit =
  createKitServer
    .listen(3000) {
      println("🚀 CKit Registry running at http://localhost:3000")
      println("📋 Health check: http://localhost:3000/health")
      println("📦 Packages API: http://localhost:3000/api/v1/packages")
    }
