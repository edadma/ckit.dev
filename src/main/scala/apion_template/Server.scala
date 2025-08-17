package apion_template

import io.github.edadma.apion.*
import zio.json.*

case class Todo(id: Int, text: String, completed: Boolean) derives JsonEncoder, JsonDecoder
case class CreateTodoRequest(text: String) derives JsonDecoder

object TodoStore:
  private var todos =
    List(
      Todo(1, "Learn Apion", false),
      Todo(2, "Build an API", false),
    )
  private var nextId = 3

  def list: Seq[Todo] = todos

  def create(text: String): Todo =
    val todo = Todo(nextId, text, false)

    nextId += 1
    todos = todo :: todos
    todo

  def toggle(id: Int): Option[Todo] =
    todos.find(_.id == id) map { todo =>
      val updated = todo.copy(completed = !todo.completed)

      todos = todos.map(t => if (t.id == id) updated else t)
      updated
    }

  def reset(): Unit =
    todos =
      List(
        Todo(1, "Learn Apion", false),
        Todo(2, "Build an API", false),
      )
    nextId = 3

def createTodoServer: Server =
  Server()
    .use(LoggingMiddleware())
    .use(CorsMiddleware())
    .get("/todos", _ => TodoStore.list.asJson)
    .post(
      "/todos",
      _.json[CreateTodoRequest].flatMap {
        case Some(CreateTodoRequest(text)) => TodoStore.create(text).asJson(201)
        case _                             => "Invalid request".asText(400)
      },
    )
    .post(
      "/todos/:id/toggle",
      _.params("id").toIntOption match
        case Some(id) =>
          TodoStore.toggle(id) match
            case Some(todo) => todo.asJson
            case None       => "Todo not found".asText(404)
        case _ => failValidation("expected integer 'id'"),
    )
