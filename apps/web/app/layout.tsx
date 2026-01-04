import "./globals.css";
import React from "react";

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
                            <span className="text-sm tracking-wider uppercase text-white/60">
                              Sebastiano Fazzino
                            </span>

                            <nav className="flex gap-6 text-sm tracking-wide text-white/60">
                                <a href="#about" className="hover:text-white">
                                    About
                                </a>
                                <a href="#experience" className="hover:text-white">
                                    Experience
                                </a>
                                <a href="#infrastructure" className="hover:text-white">
                                    Infrastructure
                                </a>
                                <a href="#contact" className="hover:text-white">
                                    Contact
                                </a>
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
