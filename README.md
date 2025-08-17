# Apion Starter Template

A minimal starter template demonstrating [Apion](https://github.com/edadma/apion), the type-safe HTTP server framework for Scala.js.

## Features

- Simple Todo API with in-memory storage
- CORS and logging middleware
- Type-safe request/response handling with zio-json
- Hot reloading development setup

## Getting Started

1. Clone this repository:
```bash
git clone https://github.com/edadma/apion-template.git
cd apion-template
```

2. Install Node.js dependencies:
```bash 
npm install
```

3. Start development servers:

In first terminal (Scala.js compiler in watch mode):
```bash 
sbt ~fastLinkJS
```

In second terminal (Node.js server with hot reloading):
```bash
npm start
```

The server will run at http://localhost:3000

## Testing the API

In a third terminal, test the endpoints:

```bash
# Get todos
curl http://localhost:3000/todos

# Create todo
curl -X POST http://localhost:3000/todos \
  -H "Content-Type: application/json" \
  -d '{"text": "Write docs"}'

# Toggle todo completion
curl -X POST http://localhost:3000/todos/1/toggle
```

## Project Structure

```
.
├── src/
│   ├── main/scala/                      # Main source code
│   │   └── apion_template/
│   │       ├── Main.scala               # Server entry point
│   │       ├── TodoServer.scala         # Server implementation
│   └── test/scala/                      # Test source code
│       └── apion_template/
│           └── TodoServerSpec.scala     # Integration tests
│
├── project/                             # sbt build helpers
│   ├── build.properties                 # sbt version
│   └── plugins.sbt                      # sbt plugins
│
├── package.json                         # Node.js dependencies & scripts
├── nodemon.json                         # Hot reload configuration
├── build.sbt                            # Scala build configuration
└── README.md                            # Project documentation
```

## Understanding the Code

The template implements a simple Todo API with these components:

- `Todo` and `CreateTodoRequest` case classes for type-safe data handling
- `TodoStore` object for in-memory storage
- Server setup with:
    - Logging middleware for request/response logging
    - CORS middleware for cross-origin requests
    - Route handlers for listing, creating, and toggling todos
    - JSON encoding/decoding with zio-json

## Available Scripts

- `npm start` - Start Node.js server with hot reloading
- `npm run kill` - Kill server process on port 3000
- `npm test` - Run tests

## Next Steps

- Add authentication with JWT
- Connect to a database
- Add request validation
- Enable compression
- Add security headers
- Add tests

See the [Apion documentation](https://edadma.github.io/apion) for more features and examples.

## License

ISC