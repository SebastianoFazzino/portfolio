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
                            Senior Software Engineer working on backend systems, cloud
                            infrastructure, and developer platforms.
                        </p>

                        <p className="mt-4 text-sm text-white/50 max-w-2xl">
                            This portfolio is deployed on my own Raspberry Pi and maintained
                            through automated CI/CD pipelines.
                        </p>
                    </div>

                    <div className="flex justify-start md:justify-end">
                        <Image
                            src="/images/profile.jpg"
                            alt="Portrait of Sebastiano Fazzino"
                            width={240}
                            height={300}
                            className="grayscale object-cover"
                            priority
                        />
                    </div>
                </div>
            </Section>

            <Section>
                <h2>About</h2>

                <p className="mt-6 max-w-3xl">
                    I’m a senior software engineer with over five years of professional
                    experience building and operating backend systems in production
                    environments.
                </p>

                <p className="mt-4 max-w-3xl">
                    My work focuses on distributed systems, cloud infrastructure, and
                    developer platforms, with a strong emphasis on reliability, observability,
                    and long-term maintainability.
                </p>

                <p className="mt-4 max-w-3xl">
                    I’ve worked across high-traffic domains including iGaming and crypto,
                    contributing to systems handling real-time data, asynchronous workflows,
                    and performance-critical workloads.
                </p>
            </Section>

            <Section>
                <h2>Experience</h2>

                <div className="mt-8 space-y-10 max-w-3xl">
                    <div>
                        <h3>Senior Software Engineer — iGaming</h3>
                        <p className="mt-2 text-sm text-white/50">
                            Backend & full-stack · Java / Kotlin · Angular · MongoDB · PostgreSQL · Redis
                        </p>
                        <p className="mt-4">
                            Worked on high-traffic production systems, focusing on backend services,
                            data consistency, and operational reliability in regulated environments.
                        </p>
                    </div>

                    <div>
                        <h3>Software Engineer — Crypto Startup</h3>
                        <p className="mt-2 text-sm text-white/50">
                            Microservices · Java · Kafka · MongoDB · PostgreSQL · GCP
                        </p>
                        <p className="mt-4">
                            Contributed to distributed microservices handling asynchronous workflows,
                            event-driven architectures, and real-time data processing.
                        </p>
                    </div>
                </div>
            </Section>

            <Section>
                <h2>Infrastructure</h2>

                <p className="mt-6 max-w-3xl">
                    This portfolio is not hosted on a managed platform. It runs on my own
                    Raspberry Pi, deployed and operated as a small production system.
                </p>

                <p className="mt-4 max-w-3xl">
                    The application stack is containerized using Docker Compose and deployed
                    automatically through GitHub Actions. Each service is built, tested, and
                    deployed independently, with targeted restarts to minimize downtime.
                </p>

                <p className="mt-4 max-w-3xl">
                    The backend services are isolated on a private network and exposed only
                    through a reverse proxy. Secrets are managed through environment variables,
                    and the system is designed with clear operational boundaries and documented
                    trade-offs.
                </p>
            </Section>

            <Section>
                <div id="contact">
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
                </div>
            </Section>

        </>
    );
}
