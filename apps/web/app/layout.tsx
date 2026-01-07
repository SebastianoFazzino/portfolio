'use client';

import "./globals.css";
import "./nav.css";
import React from "react";
import {Header} from "@/components/Header";

export default function RootLayout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" className="dark">
        <body>
        <div className="min-h-screen flex flex-col">
            <Header />

            <main className="flex-1">{children}</main>

            <footer className="border-t border-white/10">
                <div className="max-w-5xl mx-auto px-6 py-8 text-sm text-white/50">
                    © {new Date().getFullYear()} — Built & hosted on my home server.
                </div>
            </footer>
        </div>
        </body>
        </html>
    );
}
