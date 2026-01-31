# LLM‑Powered Contact Moderation (Local, Self‑Hosted)

This project includes a **local Large Language Model (LLM) moderation layer** used to protect the contact form from abuse, spam, and malicious payloads — **without relying on third‑party APIs**.

The goal is to demonstrate:
- real‑world LLM integration
- security‑oriented prompt engineering
- deterministic backend control
- efficient self‑hosting on constrained hardware (Raspberry Pi 4, 8GB RAM)

---

## Why an LLM for a Contact Form?

A basic contact form is an easy attack surface:
- spam and marketing bots
- phishing attempts
- social‑engineering messages
- SQL / shell injection payloads
- harassment or abusive language

Traditional approaches (regexes, blocklists) are:
- brittle
- easy to bypass
- hard to maintain

This system uses an **LLM as a semantic classifier**, while **keeping final authority in code**.

---

## High‑Level Architecture

```
Next.js (FE)
   ↓
Spring Boot API (Kotlin)
   ↓
Moderation Service
   ↓
Ollama (Local LLM)
```

Key properties:
- no external API calls
- no user data leaves the machine
- predictable latency
- full control over behavior

---

## The Role of the LLM

The LLM **does not decide business logic**.

Instead, it:
- classifies messages as `ALLOW` or `BLOCK`
- assigns a **single, explicit reason code** when blocking

The backend:
- validates the response
- enforces schema correctness
- rejects invalid or malformed outputs

This avoids hallucinated behavior or policy drift.

---

## Supported Block Reasons

The classifier supports the following **closed set** of reasons:

- `harassment` — insults, abuse, profanity aimed at a person
- `hate` — hateful or dehumanizing language
- `sexual` — sexual content or solicitation
- `spam` — unsolicited marketing or promotion
- `scam` — crypto, investment, or guaranteed‑profit pitches
- `phishing` — impersonation, fake verification, urgency
- `malware` — requests to run code, install tools, access systems
- `injection` — SQL fragments, shell commands, payload‑like text
- `threat` — intimidation or implied/explicit harm

If multiple apply, the model is instructed to choose **the highest‑priority reason**.

---

## Prompt Design Principles

The prompt is intentionally:

- **flat** (no deep nesting)
- **example‑driven**
- **placeholder‑free**
- **enum‑bound**

The model is shown **only valid JSON outputs** it is allowed to produce.

This avoids common failure modes such as:
- invented labels
- copied placeholder text
- verbose explanations
- false positives on greetings

The prompt is stored as a **versioned resource file**, not inline code.

---

## Prompt Loading Strategy

The moderation prompt is:

- stored in `src/main/resources/prompts/`
- loaded **once at application startup**
- hashed and logged (SHA‑256 fingerprint)

Benefits:
- zero per‑request IO
- deterministic behavior
- easy iteration via versioned prompt files

---

## Local Model Choice

The project uses:

```
llama3.2:1b
```

Why:
- smallest model that reliably follows strict output contracts
- runs comfortably under ~3 GB RAM
- good instruction adherence for classification tasks
- fast enough on ARM CPU

Smaller models (≤0.5B) were tested and rejected due to:
- enum violations
- hallucinated labels
- inconsistent abuse detection

---

## Ollama Setup (Docker)

Ollama is run locally via Docker.

Minimal compose service:

```yaml
services:
  ollama:
    image: ollama/ollama:latest
    container_name: ollama
    restart: unless-stopped
    ports:
      - "11434:11434"
    volumes:
      - portfolio_ollama:/root/.ollama
    environment:
      OLLAMA_NUM_PARALLEL: "1"
      OLLAMA_NUM_THREADS: "2"
    mem_limit: 3g
    cpus: 2
    command: ["serve"]

volumes:
  portfolio_ollama:
```

Model is pulled **once** during provisioning:

```bash
docker exec -it ollama ollama pull llama3.2:1b
```

---

## Why Self‑Hosting on a Raspberry Pi?

This setup intentionally avoids:
- Vercel / serverless abstractions
- managed AI APIs
- opaque infrastructure

Instead, it demonstrates:
- full‑stack ownership
- resource‑aware engineering
- real deployment constraints
- privacy‑first design

The Raspberry Pi becomes:
- API server
- database host
- LLM inference node

All in one.

---

## Failure Handling & Safety

If the LLM:
- returns invalid JSON
- violates the schema
- omits a reason on `BLOCK`
- produces an unknown label

The backend **fails closed** and rejects the message.

The system never trusts the model blindly.

---

## Summary

This moderation layer showcases:
- practical LLM integration
- secure prompt engineering
- deterministic backend control
- local AI inference under tight constraints

It is intentionally more complex than necessary for a portfolio site —
because the goal is to demonstrate **how production‑grade systems are built**, not just that they work.

---

## Key Takeaway

> LLMs are powerful tools — but only when treated as **untrusted components**  
> inside a system designed to constrain them.
