"use client";

import {useRef, useState} from "react";
import {Chat} from "./Chat";

export function ChatLauncher() {
    const [open, setOpen] = useState(false);

    const panelRef = useRef<HTMLDivElement | null>(null);
    const bubbleRef = useRef<HTMLButtonElement | null>(null);

    return (
        <>
            <button
                ref={bubbleRef}
                onClick={() => setOpen(prev => !prev)}
                className="
                    fixed bottom-8 right-8 z-50
                    h-16 w-16 rounded-full
                    border border-white/10
                    bg-black/60 backdrop-blur
                    text-2xl text-white/70
                    hover:text-(--accent) hover:border-(--accent)
                    cursor-pointer
                    transition-colors
                "
                aria-label="Toggle chat"
            >
                💬
            </button>

            {open && (
                <div
                    ref={panelRef}
                    className="
                        fixed
                        right-20
                        bottom-22
                        z-50
                        w-[92vw]
                        max-w-none
                        sm:max-w-115
                        md:max-w-125
                        rounded-xl
                        border border-white/10
                        bg-black/80
                        backdrop-blur
                        shadow-xl
                    "
                >
                    <div className="flex items-center justify-between px-4 py-3 border-b border-white/10">
                        <h4 className="text-sm font-medium text-white/80">
                            Ask me anything
                        </h4>

                        <button
                            onClick={() => setOpen(false)}
                            className="text-white/40 hover:text-rose-500 cursor-pointer"
                            aria-label="Close chat"
                        >
                            ✕
                        </button>
                    </div>

                    <div className="p-4">
                        <Chat />
                    </div>
                </div>
            )}
        </>
    );
}
