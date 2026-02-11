"use client";

import { useEffect, useRef, useState } from "react";

export function Chat() {
    const [question, setQuestion] = useState("");
    const [answer, setAnswer] = useState<string | null>(null);
    const [status, setStatus] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const esRef = useRef<EventSource | null>(null);

    useEffect(() => {
        return () => {
            esRef.current?.close();
            esRef.current = null;
        };
    }, []);

    function cancel() {
        esRef.current?.close();
        esRef.current = null;
        setLoading(false);
        setStatus("Cancelled");
    }

    function ask() {
        const q = question.trim();
        if (!q || loading) return;

        esRef.current?.close();
        esRef.current = null;

        setAnswer("");
        setStatus("Connecting…");
        setLoading(true);

        const es = new EventSource(`/api/knowledge?question=${encodeURIComponent(q)}`);
        esRef.current = es;

        es.addEventListener("status", (e) => {
            setStatus((e as MessageEvent).data);
        });

        es.addEventListener("token", (e) => {
            const raw = (e as MessageEvent).data as string;
            const chunk = (JSON.parse(raw) as { text: string }).text;
            setAnswer((prev) => (prev ?? "") + chunk);
        });

        es.addEventListener("done", () => {
            es.close();
            esRef.current = null;
            setLoading(false);
            setStatus(null);
        });

        es.addEventListener("error", () => {
            es.close();
            esRef.current = null;
            setLoading(false);
            setStatus("Something went wrong");
        });
    }

    return (
        <div className="border border-white/10 rounded-lg p-6 bg-black/40">
            <p className="chat mt-2 text-xs text-white/50 max-w-sm leading-relaxed">
                This chat uses my personal knowledge base to answer questions
                about my experience, projects, and how I work.
            </p>

            <div className="mt-6 space-y-4">
                <textarea
                    value={question}
                    onChange={(e) => setQuestion(e.target.value)}
                    placeholder="Ask me anything..."
                    className="
                        w-full min-h-20
                        rounded-md
                        bg-black/60
                        border border-white/10
                        p-3
                        text-sm
                        text-white
                        placeholder-white/40
                        focus:outline-none
                        focus:ring-1
                        focus:ring-(--accent)
                    "
                />

                <div className="flex items-center gap-3">
                    <button
                        onClick={ask}
                        disabled={!question.trim() || loading}
                        className="
                            inline-flex items-center justify-center
                            rounded-md
                            bg-(--accent)
                            px-4 py-2
                            text-sm font-medium
                            text-black
                            disabled:opacity-50
                        "
                    >
                        Ask
                    </button>

                    {loading && (
                        <button
                            onClick={cancel}
                            className="
                                inline-flex items-center justify-center
                                rounded-md
                                border border-white/10
                                bg-black/60
                                px-4 py-2
                                text-sm font-medium
                                text-white/80
                                hover:border-rose-500 hover:text-rose-500
                            "
                        >
                            Cancel
                        </button>
                    )}

                    {status && (
                        <span className="text-xs text-white/50">
                            {status}
                        </span>
                    )}
                </div>

                {answer !== null && (
                    <div className="mt-4 rounded-md border border-white/10 bg-black/60 p-4">
                        <p className="text-sm text-white/80 whitespace-pre-wrap">
                            {answer}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}