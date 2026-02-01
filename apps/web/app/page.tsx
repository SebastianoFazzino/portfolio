import Image from "next/image";
import {ContactModal} from "@/components/ContactModal";
import {Section} from "@/components/Section";
import {Metadata} from "next";

export const metadata: Metadata = {
    title: "Sebastiano Fazzino — Software Engineer"
};

export default function Home() {
    return (
        <>
            <Section>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
                    {/* Text */}
                    <div>
                        <h2>Who am I</h2>

                        <p className="mt-6 max-w-2xl text-lg">
                            Senior software engineer focused on designing and operating systems
                            that are reliable, observable, and built to last.
                        </p>

                        <p className="mt-4 text-sm text-white/60 max-w-2xl">
                            I work primarily on backend services, while also contributing directly
                            to frontend applications and user-facing features.
                        </p>

                        <p className="mt-4 text-sm text-white/50 max-w-2xl">
                            This site is a small, personal project — a place to document how I build,
                            think, and iterate on software.
                        </p>
                    </div>

                    {/* Image card */}
                    <div className="flex justify-start md:justify-end">
                        <div className="relative w-60 h-70 rounded-2xl overflow-hidden
                      border border-white/10 bg-white/5 shadow-sm">
                            <Image
                                src="/images/profile2.jpg"
                                alt="Sebastiano Fazzino profile picture"
                                fill
                                priority
                                className="object-cover contrast-105 brightness-95 saturation-90"
                            />

                            {/* subtle grounding gradient */}
                            <div className="absolute inset-0 bg-linear-to-t
                        from-black/30 via-transparent to-transparent" />
                        </div>
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
                            Backend · Java · Kafka · MongoDB · PostgreSQL · GCP · Docker · Kubernetes
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
                    <li>Developing Applications with Google Cloud — Google</li>
                    <li>Java Programming and Software Engineering — Duke University</li>
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

            <Section id="connect">
                <div className="pb-40">
                    <h2>Connect</h2>

                    <div className="mt-10 grid grid-cols-1 md:grid-cols-2 gap-12 max-w-4xl">
                        {/* Left: presence */}
                        <div>
                            <p className="text-white/70 max-w-md">
                                You can find me online or reach out directly.
                                I’m always open to thoughtful conversations around engineering,
                                systems, and collaboration.
                            </p>

                            <div className="mt-6 flex flex-col gap-3">
                                <a
                                    href="https://www.linkedin.com/in/sebastiano-fazzino/"
                                    target="_blank"
                                    rel="noreferrer"
                                    className="group inline-flex items-center gap-2 text-white/60 hover:text-white"
                                >
                                    <span className="text-white/40 group-hover:text-white/60">→</span>
                                    LinkedIn
                                </a>

                                <a
                                    href="https://github.com/SebastianoFazzino"
                                    target="_blank"
                                    rel="noreferrer"
                                    className="group inline-flex items-center gap-2 text-white/60 hover:text-white"
                                >
                                    <span className="text-white/40 group-hover:text-white/60">→</span>
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
                                If you prefer a direct message, you can send one here.
                                It goes straight to my inbox.
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
