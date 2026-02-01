"use client";

import React, {useEffect, useState} from "react";

export function ContactForm() {
    const [status, setStatus] = useState<"idle" | "sending" | "sent" | "error">("idle");
    const [errorMessage, setErrorMessage] = useState<string>("");

    useEffect(() => {
        if (status === "sent" || status === "error") {
            const timeout = setTimeout(() => {
                setStatus("idle");
            }, 2000);

            return () => clearTimeout(timeout);
        }
    }, [status]);

    async function onSubmit(e: React.SyntheticEvent<HTMLFormElement>) {
        e.preventDefault();
        setStatus("sending");

        const form = e.currentTarget;
        const data = new FormData(form);

        const payload = {
            name: String(data.get("name") || ""),
            email: String(data.get("email") || ""),
            message: String(data.get("message") || ""),
            website: String(data.get("website") || ""), // honeypot
        };

        const res = await fetch("/api/contact", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        let responseBody: { message?: string } | null;

        try {
            responseBody = await res.json();
        } catch {
            responseBody = null;
        }

        if (res.ok) {
            form.reset();
            setStatus("sent");
        } else {
            setStatus("error");
            setErrorMessage(responseBody?.message ?? "Something went wrong.");
        }
    }

    return (
        <form onSubmit={onSubmit} className="mt-8 max-w-3xl space-y-4">
            {/* Honeypot: visually hidden */}
            <input
                name="website"
                tabIndex={-1}
                autoComplete="off"
                className="hidden"
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <input
                    name="name"
                    required
                    placeholder="Name"
                    className="w-full bg-black/40 border border-white/10 rounded px-3 py-2 text-white"
                />
                <input
                    name="email"
                    type="email"
                    required
                    placeholder="Email"
                    className="w-full bg-black/40 border border-white/10 rounded px-3 py-2 text-white"
                />
            </div>

            <textarea
                name="message"
                required
                placeholder="Message"
                rows={6}
                className="w-full bg-black/40 border border-white/10 rounded px-3 py-2 text-white"
            />

            <div className="flex items-center gap-4">
                <button
                    type="submit"
                    disabled={status === "sending"}
                    className="px-4 py-2 rounded cursor-pointer bg-white text-black disabled:opacity-50"
                >
                    {status === "sending" ? "Sending…" : "Send"}
                </button>

                <p className="text-sm text-white/50">
                    {status === "sent" && "Message sent."}
                    {status === "error" && <span className="text-red-500">{errorMessage}</span>}
                </p>
            </div>
        </form>
    );
}
