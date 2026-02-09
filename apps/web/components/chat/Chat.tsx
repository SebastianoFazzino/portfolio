"use client";

import { useState } from "react";

export function Chat() {
    const [question, setQuestion] = useState("");
    const [answer, setAnswer] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

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

                <button
                    disabled={!question || loading}
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

                {answer && (
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
