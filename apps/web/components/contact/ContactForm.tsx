"use client";

import React, {useEffect, useState} from "react";

type ContactFormProps = {
    onSentAction: () => void;
};

type ContactApiResponse =
    | { ok: true }
    | { ok: false; errorCode?: string; message?: string };

export function ContactForm({ onSentAction }: ContactFormProps) {
    const [status, setStatus] = useState<"idle" | "sending" | "sent" | "error">("idle");
    const [errorMessage, setErrorMessage] = useState<string>("");

    useEffect(() => {
        if (status !== "sent") return;
        const timeout = setTimeout(() => {
            onSentAction();
        }, 2000);
        return () => clearTimeout(timeout);
    }, [status, onSentAction]);

    useEffect(() => {
        if (status === "sent" || status === "error") {
            const timeout = setTimeout(() => {
                setStatus("idle");
            }, 4000);

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

        let responseBody: ContactApiResponse | null;

        try {
            responseBody = (await res.json()) as ContactApiResponse;
        } catch {
            responseBody = null;
        }

        if (res.ok) {
            form.reset();
            setStatus("sent");
        } else {
            setStatus("error");

            const code = responseBody && "ok" in responseBody && !responseBody.ok ? responseBody.errorCode : undefined;

            if (code === "contact_rejected") {
                setErrorMessage("That message can’t be sent as written. Try rephrasing (no links, commands, or aggressive language).");
            } else if (code === "too_many_requests") {
                setErrorMessage("Too many attempts. Please wait a moment and try again.");
            } else {
                setErrorMessage(responseBody && "message" in responseBody ? responseBody.message ?? "Something went wrong." : "Something went wrong.");
            }
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
                    className="input-accent w-full rounded px-3 py-2"
                />
                <input
                    name="email"
                    type="email"
                    required
                    placeholder="Email"
                    className="input-accent w-full rounded px-3 py-2"
                />
            </div>

            <textarea
                name="message"
                required
                placeholder="Message"
                rows={6}
                className="input-accent w-full rounded px-3 py-2"
            />

            <div className="flex items-center gap-4">
                <button
                    type="submit"
                    disabled={status === "sending"}
                    className={`px-4 py-2 rounded text-black transition-colors cursor-pointer disabled:opacity-80 min-w-30
                        ${status === "idle" || status === "sent" || status === "error"
                            ? "bg-white hover:bg-(--accent) active:bg-(--accent)"
                            : "bg-(--accent)"
                        }
                    `}
                >
                    {status === "sending" ? "Sending…" : "Send"}
                </button>

                <div className="text-sm text-white/50">
                    {status === "sent" && "Message sent."}

                    {/* Show error */}
                    {status === "error" && (
                        <div className="mt-4 rounded-lg border border-rose-500/20 bg-rose-500/5 p-3 text-sm text-rose-200/80">
                            <div className="flex items-start gap-2">
                                <span className="text-rose-300/60">!</span>
                                <div>
                                    <p>{errorMessage}</p>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </form>
    );
}
