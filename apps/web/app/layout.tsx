'use client';

import "./globals.css";
import "./nav.css";
import React from "react";

function scrollToTop() {
    history.replaceState(null, "", window.location.pathname + window.location.search);
    document.body.classList.add("nav-reset");
    window.scrollTo({ top: 0, behavior: "smooth" });
}

export default function RootLayout(
    {children,}: { children: React.ReactNode; }) {
    return (
        <html lang="en" className="dark">
            <body>
                {/*Page wrapper */}
                <div className="min-h-screen flex flex-col">

                    {/* Header */}
                    <header className="sticky top-0 z-50 border-b border-white/10 bg-black/60 backdrop-blur">
                        <div className="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
                            <button
                                onClick={scrollToTop}
                                className="text-sm tracking-wider uppercase text-white/60 hover:text-white"
                                aria-label="Scroll to top"
                            >
                                Sebastiano Fazzino
                            </button>

                            <nav className="flex gap-6 text-sm tracking-wide text-white/60">
                                <nav className="flex gap-6 text-sm tracking-wide text-white/60">
                                    <a href="#about" data-target="about" className="nav-link">
                                        About
                                    </a>
                                    <a href="#experience" data-target="experience" className="nav-link">
                                        Experience
                                    </a>
                                    <a href="#certifications" data-target="certifications" className="nav-link">
                                        Certifications
                                    </a>
                                    <a href="#this-site" data-target="this-site" className="nav-link">
                                        This Site
                                    </a>
                                    <a href="#contact" data-target="contact" className="nav-link">
                                        Contact
                                    </a>
                                </nav>
                            </nav>
                        </div>
                    </header>

                    {/* Main content */}
                    <main className="flex-1">
                        {children}
                    </main>

                    {/* Footer placeholder */}
                    <footer className="border-t border-white/10">
                        <div className="max-w-5xl mx-auto px-6 py-8 text-sm text-white/50">
                            © {new Date().getFullYear()} — Built & hosted on my own infrastructure
                        </div>
                    </footer>

                </div>
            </body>
        </html>
    );
}
