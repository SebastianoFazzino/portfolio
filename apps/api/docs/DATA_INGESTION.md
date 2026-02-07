# Knowledge ingestion guide (KnowledgeIndexService)

This document explains how to prepare and ingest knowledge files so the
`KnowledgeIndexService` can index them into a vector store (e.g., PostgreSQL + pgvector).

This guide and the sample file are **generic** and suitable for open-source use. Replace the
sample content with your own portfolio or project knowledge as needed.

## Where knowledge files live

All knowledge files must be placed under:

resources/knowledge/

The indexer is expected to scan this directory and ingest every valid knowledge file it finds.

A complete example file is provided at:

resources/knowledge/example-knowledge.txt

Use that file as the reference format when adding or modifying knowledge.

## What gets indexed

The indexer stores **chunks**: small-to-medium text units that the retriever can pull into
the LLM context.

A good chunk:
- Contains one coherent idea (or one idea with a supporting detail)
- Is 1–2 sentences long
- Targets roughly 250–500 characters
- Avoids repeating the same idea across multiple chunks

Knowledge is typically written in **third person** (facts about the portfolio owner).
The LLM prompt can convert answers to **first person** at response time.

## File format expected by the indexer

Each knowledge file is composed of one or more **sources**.

A source is a named group of related chunks (for example:
`experience_profile_v1`, `architecture_general_v1`, `this_portfolio_v1`).

Each source block must follow this structure exactly:

```
source: <source_name>
---
- <chunk 1>
- <chunk 2>
- <chunk 3>
```

Rules:
- `source:` must start at the beginning of the line
- `---` must appear on its own line immediately after the source name
- Each chunk must start with `- ` (dash followed by a space)
- Blank lines between chunks are allowed
- Source names should be stable; use `_v1`, `_v2` for major revisions

## Chunking guidelines

### Prefer small-to-medium chunks

Large paragraphs tend to:
- Retrieve loosely relevant text too often
- Waste context budget
- Produce generic answers due to summarization

Small-to-medium chunks enable:
- Precise retrieval
- Better mixing of details
- More natural, varied answers

### Make chunks factual and specific

Prefer concrete details, decisions, and constraints.

Good examples:
- “The engineer has used Kafka for asynchronous communication between microservices.”
- “The engineer has worked in domains with different reliability and performance constraints.”

Avoid vague statements like:
- “The engineer is passionate about learning.”

## Updating knowledge safely

When modifying knowledge:
- Prefer adding new chunks to increase coverage, or
- Create a new source version (e.g., `_v2`) for substantial rewrites

If the indexer deduplicates by `contentHash`, unchanged chunks will not be re-embedded.

## Suggested source layout

Common sources include:
- experience_profile_v1
- experience_backend_v1
- experience_frontend_v1
- experience_databases_v1
- architecture_general_v1
- this_portfolio_v1
- ai_usage_v1
- personal_v1

## Validation checklist

After ingestion, validate with realistic queries:
- “What kind of systems do you build?”
- “What is your experience with Spring Boot?”
- “Have you worked with microservices or distributed systems?”
- “What is the architecture of this portfolio?”

If answers feel repetitive:
- Add more coverage (more chunks)
- Reduce near-duplicate chunks
- Increase retrieval topK slightly and enforce diversity
