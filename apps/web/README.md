# Personal Portfolio

This repository contains the source code for my personal engineering portfolio.

The site is intentionally minimal: it focuses on clarity, structure, and content rather than visual effects or marketing language. It is meant to read more like an editorial page than a product or CV.

---

## Overview

- Built with **Next.js** using the App Router
- Dark-only, minimal UI
- Typography- and content-first layout
- No client-side state libraries, no heavy UI frameworks
- Navigation highlights the currently viewed section based on scroll position

The goal of the project is not to showcase every technology I know, but to present how I think about building and structuring software.

---

## Tech stack

### Frontend
- **Next.js** (App Router)
- React Server Components where possible
- Minimal client-side JavaScript
- Custom CSS with a restrained design system
- No component libraries

### Styling
- Dark-only theme
- Subtle accent color
- Simple layout primitives
- Focus on spacing, hierarchy, and readability

The UI is intentionally quiet and avoids animations or visual noise.

---

## End-to-end testing

The project includes **end-to-end tests** implemented with **Playwright**.

The goal of the test suite is not exhaustive coverage, but **high-signal guarantees** that critical user-facing behavior remains intact.

### What is tested

- Homepage loads successfully
- No console errors occur on initial render
- Primary navigation buttons scroll to the correct sections
- The main call-to-action (“Connect”) behaves correctly
- Core interactions work across browsers (Chromium, WebKit, mobile Safari where enabled)

Tests are written to reflect **real user behavior**, not implementation details:
- No CSS selectors or class names are asserted
- Elements are located by role, accessible name, or semantic structure
- Tests avoid timing-based assumptions and brittle waits

### Philosophy

The test suite is intentionally small and focused:

- Tests validate *outcomes*, not animations or layout details
- Scroll and navigation behavior is verified in a cross-browser-safe way
- The suite is designed to be boring, fast, and reliable

If a test fails, it should indicate a real regression rather than a cosmetic change.

### Running tests locally

From the `apps/web` directory:

```bash
npx playwright test
```

To run tests interactively with the Playwright UI:

```bash
npx playwright test --ui
```

---

## Contact form

The site includes a server-side contact form.

- Messages are sent via **Brevo** (transactional email API)
- Email sending is handled on the server only
- No API keys or secrets are exposed to the client
- Basic spam protection (honeypot + rate limiting)

### Environment variables

The following environment variables are required:

```bash
BREVO_API_KEY=
CONTACT_FROM_EMAIL=
CONTACT_TO_EMAIL=
CONTACT_FROM_NAME=
```

A `.env.example` file is provided. Real values must be supplied via environment configuration and are not committed to the repository.

---

## Architecture notes

- No client-side form submission directly to third-party services
- All external integrations go through server-side route handlers
- Secrets are managed exclusively via environment variables
- The project is suitable for self-hosting or deployment on any Node-compatible platform

---

## Development

Install dependencies:

```bash
npm install
```

Run the development server:

```bash
npm run dev
```

Build for production:

```bash
npm run build
npm start
```

---

## License

This project is released under the **MIT License**.

Feel free to explore, reuse ideas, or adapt parts of the implementation for your own projects.
