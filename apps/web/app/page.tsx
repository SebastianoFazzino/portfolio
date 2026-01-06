import Image from "next/image";
import { Section } from "@/components/Section";

export default function Home() {
    return (
        <>
            <Section>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
                    <div>
                        <h2>Who I am</h2>

                        <p className="mt-6 max-w-2xl">
                            Senior software engineer focused on designing and operating systems
                            that are reliable, observable, and built to last.
                        </p>

                        <p className="mt-4 text-sm text-white/50 max-w-2xl">
                            I work primarily on backend services, while also contributing directly
                            to frontend applications and user-facing features.
                        </p>

                        <p className="mt-4 text-sm text-white/50 max-w-2xl">
                            This site is a small, personal project, a place to document how I build, think,
                            and iterate on software.
                        </p>
                    </div>

                    <div className="flex justify-start md:justify-end">
                        <Image
                            src="/images/profile.jpg"
                            alt="Sebastiano Fazzino profile picture"
                            width={240}
                            height={260}
                            className="grayscale object-cover"
                            priority
                        />
                    </div>
                </div>
            </Section>

            <Section id="about">
                <h2>About</h2>

                <p className="mt-6 max-w-3xl">
                    I’m a senior software engineer with over five years of experience working on
                    production systems.
                </p>

                <p className="mt-4 max-w-3xl">
                    Most of my work is on backend services: designing APIs, working with data,
                    and keeping systems maintainable, testable and reliable over time.
                </p>

                <p className="mt-4 max-w-3xl">
                    I’ve worked in iGaming, fintech, and crypto, on systems with high throughput,
                    sensitive data, strict security requirements, and multiple external integrations.
                </p>
            </Section>

            <Section id="experience">
                <h2>Experience</h2>

                <div className="mt-8 space-y-10 max-w-3xl">
                    <div>
                        <h3>Senior Software Engineer — iGaming</h3>
                        <p className="mt-2 text-sm text-white/50">
                            Backend & full-stack · Java / Kotlin · Angular · MongoDB · PostgreSQL · Redis
                        </p>
                        <p className="mt-4">
                            Worked on core backend services in a regulated environment, with a focus
                            on data consistency, system reliability, and day-to-day operational stability.
                        </p>
                    </div>

                    <div>
                        <h3>Software Engineer — Crypto</h3>
                        <p className="mt-2 text-sm text-white/50">
                            Backend · Java · Kafka · MongoDB · PostgreSQL · Cloud platforms
                        </p>
                        <p className="mt-4">
                            Built and evolved services integrating with external systems, handling
                            asynchronous processing and data flows across multiple components.
                        </p>
                    </div>
                </div>
            </Section>

            <Section id="certifications">
                <h2>Certifications</h2>

                <ul className="mt-6 space-y-3 max-w-2xl text-white/70">
                    <li>Java Programming and Software Engineering — Duke University</li>
                    <li>Developing Applications with Google Cloud — Google</li>
                </ul>

                <h4 className="mt-10 text-white/50 text-sm uppercase tracking-wide">
                    In progress
                </h4>

                <ul className="mt-4 space-y-3 max-w-2xl text-white/50">
                    <li>AWS Solutions Architect – Associate</li>
                    <li>AWS Developer – Associate</li>
                </ul>
            </Section>

            <Section id="this-site">
                <h2>This site</h2>

                <ul className="mt-6 space-y-3 max-w-2xl list-disc list-inside text-white/70">
                    <li>Frontend built with Next.js</li>
                    <li>Self-hosted on my own Raspberry Pi (no managed hosting)</li>
                    <li>Containerized with Docker Compose</li>
                    <li>Automated builds & deployments via GitHub Actions</li>
                    <li>Reverse proxy in front, backend services kept private</li>
                    <li>Secrets managed through environment variables</li>
                </ul>

                <p className="mt-6 text-sm text-white/50 max-w-2xl">
                    Designed as a small, self managed system with clear operational boundaries.
                </p>
            </Section>

            <Section id="contact">
                <div id="contact" className="scroll-mt-24" />
                <h2>Contact</h2>

                <p className="mt-6 max-w-3xl">
                    If you’d like to get in touch to discuss engineering work, systems design,
                    or collaboration opportunities, feel free to reach out.
                </p>

                <p className="mt-4 max-w-3xl">
                    This site includes a server-side contact form to avoid exposing personal
                    email addresses publicly.
                </p>

                <p className="mt-6 text-sm text-white/50">
                    Contact form coming next.
                </p>
            </Section>
        </>
    );
}
