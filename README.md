# Intelligent Knowledge Graph RAG Assistant

A beginner-friendly monolithic Spring Boot GraphRAG project.

## Requirements

- Java 17+
- Maven 3.9+
- PostgreSQL with the `pgvector` extension
- Neo4j 5.x
- OpenAI API key for LLM answers and production embeddings (optional while learning)

No Docker, Redis, Kafka, Kubernetes, Spring Security, or microservices are required.

## Run the databases

Create a PostgreSQL database named `graphrag`, install pgvector, and set these environment variables if your credentials differ:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/graphrag"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:NEO4J_URI="bolt://localhost:7687"
$env:NEO4J_USERNAME="neo4j"
$env:NEO4J_PASSWORD="password"
$env:OPENAI_API_KEY="your-key"
```

The application executes `schema.sql` on startup. It creates `documents`, `document_chunks`, and the pgvector column.

## Run the application

```powershell
mvn spring-boot:run
```

## API examples

```powershell
curl http://localhost:8080/api/hello

curl -F "file=@company.txt" http://localhost:8080/api/documents/upload

curl -X POST http://localhost:8080/api/rag/query `
  -H "Content-Type: application/json" `
  -d '{"question":"What does the document say about AlphaFold?"}'

curl -X POST http://localhost:8080/api/graphrag/query `
  -H "Content-Type: application/json" `
  -d '{"question":"What product was developed by the company acquired by Google?"}'

curl "http://localhost:8080/api/graph/search?question=What%20did%20Google%20acquire"
curl http://localhost:8080/api/graph/entities
```

## Example document

```text
Google acquired DeepMind.
DeepMind developed AlphaFold.
AlphaFold is used for protein structure prediction.
```

## Architecture

```text
REST Controller
    -> Service
        -> PostgreSQL/pgvector for document similarity
        -> Neo4j for entities and relationships
        -> LangChain4j for embeddings, LLM answers, and tools
```

## Learning map

- `document`: upload, text extraction, and chunking
- `rag`: embeddings and vector retrieval
- `graph`: Cypher, Neo4j nodes, and relationships
- `ai`: LangChain4j models, extraction, answers, and tools
- `service`: orchestration between the components

Without `OPENAI_API_KEY`, the application still demonstrates the flow using deterministic local embeddings, pattern-based relationship extraction, and retrieved-context fallback answers. Configure the key to activate OpenAI-backed embeddings, entity extraction, answer generation, and tool calling.