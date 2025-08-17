package dev.ckit

@main
def run(): Unit =
  createKitServer
    .listen(3000) { println("ckit.dev server running at http://localhost:3000") }
