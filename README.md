# Self-Hosted AI Portfolio

This repository contains a **fully open-source portfolio** with an
integrated, self-hosted AI assistant.

The goal of this project is not to showcase AI hype, but to demonstrate
**pragmatic system design**, clear architectural boundaries, and
end-to-end ownership of a real application.

------------------------------------------------------------------------

## What this is

A personal portfolio website with an AI assistant that can answer
questions about my background, experience, and projects.

The assistant is **retrieval-augmented**, intentionally constrained, and
designed to behave predictably rather than creatively.

In addition, AI is used selectively in supporting roles (e.g. request
moderation), where it provides clear, measurable value.

------------------------------------------------------------------------

## Why this exists

This project was built to demonstrate:

- Ownership of the full stack (frontend, backend, infrastructure)
- Thoughtful use of AI under real constraints
- Clear separation between policy, control flow, and model output
- Defensive system design with explicit guardrails
- Systems that fail gracefully and transparently

Everything here is designed to be understandable, reproducible, and
adaptable.

------------------------------------------------------------------------

## What makes it interesting

- **Self-hosted LLM** (no third-party AI APIs)
- **Retrieval-Augmented Generation (RAG)** over curated knowledge
- **Explicit moderation and guardrails**
- **AI-assisted contact form moderation** (spam, abuse, prompt-injection)
- **No server-side chat history storage**
- **Designed for limited hardware** (runs on a Raspberry Pi)

AI is treated as a component, not an authority.

------------------------------------------------------------------------

## High-level architecture

```
Browser
  ↓
Frontend (Next.js)
  ↓
Spring Boot API
  ├─ Request moderation & routing
  ├─ Contact form moderation
  ├─ Retrieval (PostgreSQL + pgvector)
  └─ Prompt assembly
  ↓
Ollama (self-hosted LLM)
```

------------------------------------------------------------------------

## Technology stack

- **Backend:** Kotlin / Java, Spring Boot
- **Database:** PostgreSQL with pgvector
- **Frontend:** Next.js
- **LLM runtime:** Ollama
- **Deployment:** Docker Compose

------------------------------------------------------------------------

## Design constraints

This project intentionally operates under the following constraints:

- Limited compute and memory
- No external AI services
- Predictable behavior over novelty
- Minimal data retention
- Clear, observable failure modes

These constraints drive most architectural decisions.

------------------------------------------------------------------------

## Getting started

Clone the repository:

```bash
git clone https://github.com/SebastianoFazzino/portfolio.git
cd portfolio
```

Start the system:

```bash
docker compose up
```

Refer to the individual service directories for configuration details.

------------------------------------------------------------------------

## Open source

This project is fully open source and licensed under **MIT**.

You are welcome to:
- Explore the code
- Fork the repository
- Adapt it for your own portfolio or projects

------------------------------------------------------------------------

## License

MIT License.
