package apion_template

@main
def run(): Unit =
  createTodoServer
    .listen(3000) { println("Todo server running at http://localhost:3000") }
