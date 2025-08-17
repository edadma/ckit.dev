package apion_template

import io.github.edadma.apion.{Server, logger}
import io.github.edadma.logger.LogLevel
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach

import scala.scalajs.js
import io.github.edadma.nodejs.{FetchOptions, fetch, Server as NodeServer}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.compiletime.uninitialized
import scala.concurrent.ExecutionContext

class TodoServerSpec extends AsyncFreeSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {
  import org.scalajs.macrotaskexecutor.MacrotaskExecutor
  implicit override def executionContext: ExecutionContext = MacrotaskExecutor

  var server: Server         = uninitialized
  var httpServer: NodeServer = uninitialized
  val port                   = 3001 // Different from dev port

  override def beforeAll(): Unit = {
    server = createTodoServer
    httpServer = server.listen(port) {}
  }

  override def beforeEach(): Unit = {
    TodoStore.reset() // Reset to initial state before each test

    logger.setLogLevel(LogLevel.OFF)
    logger.resetOpId()
  }

  override def afterAll(): Unit = {
    if (httpServer != null) {
      httpServer.close(() => ())
    }
  }

  "TodoServer" - {
    "GET /todos" - {
      "should list initial todos" in {
        fetch(s"http://localhost:$port/todos")
          .toFuture
          .flatMap(response => response.json().toFuture)
          .map { result =>
            val todos = js.JSON.stringify(result)
            todos should include("Learn Apion")
            todos should include("Build an API")
            todos should include("false") // Both are initially uncompleted
          }
      }
    }

    "POST /todos" - {
      "should create new todo" in {
        val options = FetchOptions(
          method = "POST",
          headers = js.Dictionary("Content-Type" -> "application/json"),
          body = """{"text": "Write tests"}""",
        )

        fetch(s"http://localhost:$port/todos", options)
          .toFuture
          .flatMap { response =>
            response.status shouldBe 201
            response.json().toFuture
          }
          .map { result =>
            val todo = js.JSON.stringify(result)
            todo should include("Write tests")
            todo should include("false") // not completed
          }
      }

      "should reject invalid request" in {
        val options = FetchOptions(
          method = "POST",
          headers = js.Dictionary("Content-Type" -> "application/json"),
          body = """{"invalid": "data"}""",
        )

        fetch(s"http://localhost:$port/todos", options)
          .toFuture
          .map { response =>
            response.status shouldBe 400
          }
      }
    }

    "POST /todos/:id/toggle" - {
      "should toggle todo completion" in {
        val options = FetchOptions(method = "POST")

        fetch(s"http://localhost:$port/todos/1/toggle", options)
          .toFuture
          .flatMap { response =>
            response.status shouldBe 200
            response.json().toFuture
          }
          .map { result =>
            val todo = js.JSON.stringify(result)
            todo should include("true") // Now completed
          }
      }

      "should handle not found" in {
        val options = FetchOptions(method = "POST")

        fetch(s"http://localhost:$port/todos/999/toggle", options)
          .toFuture
          .map { response =>
            response.status shouldBe 404
          }
      }

      "should handle invalid ID" in {
        val options = FetchOptions(method = "POST")

        fetch(s"http://localhost:$port/todos/invalid/toggle", options)
          .toFuture
          .map { response =>
            response.status shouldBe 400
          }
      }
    }
  }
}
