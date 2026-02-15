import Image from 'next/image';
import { ContactModal } from '@/components/contact/ContactModal';
import { Section } from '@/components/common/Section';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Sebastiano Fazzino — Software Engineer',
  description:
    'Explore my work, experience, and system design approach across backend and full-stack engineering.',
};

export default function Home() {
  return (
    <>
      <h1 className="sr-only">Full-stack software engineer building reliable systems</h1>
      <Section>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          {/* Text */}
          <div>
            <h2>Who am I</h2>

            <p className="mt-6 max-w-2xl text-lg">
              Senior software engineer focused on building backend services that are clear,
              reliable, and maintainable.
            </p>

            <p className="mt-4 text-sm text-white/60 max-w-2xl">
              I work primarily on backend services, while also contributing directly to frontend
              applications and user-facing features.
            </p>

            <p className="mt-4 text-sm text-white/50 max-w-2xl">
              This site is a small personal project where I share how I build, think, and improve
              systems.
            </p>
          </div>

          {/* Image card */}
          <div className="flex justify-start md:justify-end">
            <div
              className="relative w-60 h-70 rounded-2xl overflow-hidden
                      border border-white/10 bg-white/5 shadow-sm"
            >
              <Image
                src="/images/profile-img.jpg"
                alt="Sebastiano Fazzino profile picture"
                fill
                priority
                className="object-cover contrast-105 brightness-95 saturation-90"
              />

              {/* subtle grounding gradient */}
              <div
                className="absolute inset-0 bg-linear-to-t
                        from-black/30 via-transparent to-transparent"
              />
            </div>
          </div>
        </div>
      </Section>

      <Section id="about">
        <h2>About</h2>

        <p className="mt-6 max-w-3xl">
          I’m a senior software engineer with over five years of experience working on production
          systems.
        </p>

        <p className="mt-4 max-w-3xl">
          Most of my work is on backend services: designing APIs, working with data, and keeping
          systems maintainable, testable and reliable over time.
        </p>

        <p className="mt-4 max-w-3xl">
          I’ve worked across iGaming, Fintech, and Crypto, on systems with high throughput,
          sensitive data, strict security requirements, and multiple external integrations.
        </p>

        <p className="mt-4 max-w-3xl">
          Lately I’ve been exploring DevOps and practical AI, mostly by self-hosting, automating
          deployments, and building small experiments like the assistant on this site.
        </p>
      </Section>

      <Section id="experience">
        <h2>Experience</h2>

        <div className="mt-8 space-y-10 max-w-3xl">
          <div>
            <h3>Software Engineer — Yolo (iGaming)</h3>
            <p className="mt-1 text-sm text-white/40">Oct 2024 — Present · Tallinn</p>
            <p className="mt-2 text-sm text-white/50">
              Backend & full-stack · Java / Kotlin · Spring Boot · Angular · MongoDB · PostgreSQL ·
              Redis
            </p>
            <p className="mt-4">
              I work mainly on a core backend service responsible for player transactions and
              integrations with external partners. My focus is keeping things reliable in
              production: data consistency, operational stability, and changes that are easy to
              reason about.
            </p>
          </div>

          <div>
            <h3>Java Software Developer — Swag (crypto / fintech)</h3>
            <p className="mt-1 text-sm text-white/40">Feb 2021 — Oct 2024 · Tallinn</p>
            <p className="mt-2 text-sm text-white/50">
              Backend · Java · Spring Boot · Kafka · PostgreSQL · GCP · Docker · Kubernetes
            </p>
            <p className="mt-4">
              I worked on a microservices setup where services communicated over REST and Kafka.
              Most of my work was on backend systems around payments and order flows, plus
              integrations with internal and external services.
            </p>
          </div>
        </div>
      </Section>

      <Section id="certifications">
        <h2>Certifications</h2>

        <p className="mt-4 max-w-2xl text-sm text-white/50">
          A few courses and certifications I’ve completed recently.
        </p>

        <ul className="mt-6 space-y-3 max-w-2xl text-white/70">
          <li>Developing Applications with Google Cloud Specialization — Google (Apr 2024)</li>
          <li>Java Programming and Software Engineering — Duke University (Jan 2023)</li>
          <li>IBM Data Science Professional Certificate — IBM (Aug 2020)</li>
        </ul>

        <h4 className="mt-10 text-white/50 text-sm uppercase tracking-wide">In progress</h4>

        <ul className="mt-4 space-y-3 max-w-2xl text-white/50">
          <li>AWS Solutions Architect — Associate</li>
          <li>AWS Certified AI Practitioner</li>
        </ul>
      </Section>

      {/*<Section id="projects">*/}
      {/*  <h2>This site</h2>*/}

      {/*  <p className="mt-6 max-w-3xl text-white/80">*/}
      {/*    This site includes a lightweight AI assistant designed to answer questions about my*/}
      {/*    background, experience, and projects. It is fully self-hosted and runs on my own*/}
      {/*    infrastructure, using retrieval-augmented generation (RAG) over curated knowledge rather*/}
      {/*    than a generic chatbot.*/}
      {/*  </p>*/}

      {/*  <p className="mt-4 max-w-3xl text-white/60">*/}
      {/*    The system is intentionally constrained, moderated, and resource-aware, prioritizing*/}
      {/*    predictable behavior, data safety, and clear boundaries over novelty.*/}
      {/*  </p>*/}

      {/*  <p className="mt-4 max-w-3xl text-white/60">*/}
      {/*    I built it to demonstrate how I approach real-world system design: ownership, trade-offs,*/}
      {/*    and reliability over hype.*/}
      {/*  </p>*/}

      {/*  <div className="mt-10 max-w-3xl">*/}
      {/*    <h4 className="text-white/50 text-sm uppercase tracking-wide">Open Source</h4>*/}

      {/*    <p className="mt-4 text-white/70">*/}
      {/*      The full source code for this project is available under the MIT License. You are*/}
      {/*      welcome to fork it, reuse parts of it, or adapt it for your own portfolio.*/}
      {/*    </p>*/}

      {/*    <a*/}
      {/*      href="https://github.com/SebastianoFazzino/portfolio"*/}
      {/*      target="_blank"*/}
      {/*      rel="noreferrer"*/}
      {/*      className="group mt-4 inline-flex items-center gap-2 text-white/60 transition-colors hover:text-(--accent)"*/}
      {/*    >*/}
      {/*      <span className="text-white/40 transition-colors group-hover:text-(--accent)">→</span>*/}
      {/*      <span>View on GitHub</span>*/}
      {/*    </a>*/}
      {/*  </div>*/}
      {/*</Section>*/}

      <Section id="projects">
        <h2>Projects</h2>

        <div className="mt-8 max-w-3xl space-y-10">
          <div>
            <h3>Portfolio + AI assistant</h3>
            <p className="mt-2 text-sm text-white/50">
              Next.js · Kotlin · Self-hosted · PgVector · Lightweight RAG
            </p>

            <p className="mt-4 text-white/80">
              This site is a small, self-hosted project where I share how I build and iterate on
              software. It also includes a lightweight AI assistant that can answer questions about
              my background, experience, and projects using retrieval over curated content.
            </p>

            <p className="mt-4 text-white/60">
              I built it to show practical system design work: clear boundaries, predictable
              behavior, and an approach that’s mindful about data safety and operational overhead.
            </p>

            <div className="mt-8">
              <h4 className="text-white/50 text-sm uppercase tracking-wide">Open Source</h4>

              <p className="mt-4 text-white/70">
                The full source code for this project is available under the MIT License. You are
                welcome to fork it, reuse parts of it, or adapt it for your own portfolio.
              </p>

              <a
                href="https://github.com/SebastianoFazzino/portfolio"
                target="_blank"
                rel="noreferrer"
                className="group mt-4 inline-flex items-center gap-2 text-white/60 transition-colors hover:text-(--accent)"
              >
                <span className="text-white/40 transition-colors group-hover:text-(--accent)">
                  →
                </span>
                <span>View on GitHub</span>
              </a>
            </div>
          </div>
        </div>
      </Section>

      <Section id="connect">
        <div className="pb-40">
          <h2>Connect</h2>

          <div className="mt-10 grid grid-cols-1 md:grid-cols-2 gap-12 max-w-4xl">
            {/* Left: presence */}
            <div>
              <p className="text-white/70 max-w-md">
                You can find me online or reach out directly. I’m always open to thoughtful
                conversations around engineering, systems, and collaboration.
              </p>

              <div className="mt-6 flex flex-col gap-3">
                <a
                  href="https://www.linkedin.com/in/sebastiano-fazzino/"
                  target="_blank"
                  rel="noreferrer"
                  className="group inline-flex items-center gap-2 text-white/60 hover:text-(--accent) transition-colors"
                >
                  <span className="text-white/40 group-hover:text-(--accent)">→</span>
                  LinkedIn
                </a>

                <a
                  href="https://github.com/SebastianoFazzino"
                  target="_blank"
                  rel="noreferrer"
                  className="group inline-flex items-center gap-2 text-white/60 hover:text-(--accent) transition-colors"
                >
                  <span className="text-white/40 group-hover:text-(--accent)">→</span>
                  GitHub
                </a>
              </div>

              <div className="mt-10 max-w-md">
                <p className="mt-4 text-sm text-white/70">
                  I value thoughtful engineering, and keeping things as simple as they can be.
                </p>
                <p className="mt-4 text-sm text-white/50">
                  If that aligns with how you work, feel free to get in touch.
                </p>
              </div>
            </div>

            {/* Right: action */}
            <div className="border border-white/10 rounded-lg p-6 bg-black/40">
              <h3 className="text-lg font-semibold">Send a message</h3>

              <p className="mt-2 text-sm text-white/50 max-w-sm">
                If you prefer a direct message, you can send one here. It goes straight to my inbox.
              </p>

              <div className="mt-6">
                <ContactModal />
              </div>
            </div>
          </div>
        </div>
      </Section>
    </>
  );
}
