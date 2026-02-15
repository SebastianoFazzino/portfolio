'use client';

import { useRef, useState } from 'react';
import { Chat } from './Chat';

export function ChatLauncher() {
  const [open, setOpen] = useState(false);

  const panelRef = useRef<HTMLDivElement | null>(null);
  const bubbleRef = useRef<HTMLButtonElement | null>(null);

  return (
    <>
      <button
        ref={bubbleRef}
        onClick={() => setOpen((prev) => !prev)}
        className="
                    fixed z-50
                    bottom-4 right-4
                    sm:bottom-6 sm:right-6
                    h-14 w-14 sm:h-16 sm:w-16 rounded-full
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
                      fixed z-50
                      left-3 right-3 bottom-20
                      sm:left-auto sm:right-24 sm:bottom-24
                      w-auto sm:w-105
                      rounded-xl
                      border border-white/10
                      bg-black/80
                      backdrop-blur
                      shadow-xl
                      overflow-hidden
                    "
          style={{ maxHeight: 'calc(100dvh - 7.5rem)' }}
        >
          <div className="flex items-center justify-between px-4 py-3 border-b border-white/10">
            <h4 className="text-sm font-medium text-white/80">Ask me anything</h4>

            <button
              onClick={() => setOpen(false)}
              className="text-white/40 hover:text-rose-500 cursor-pointer"
              aria-label="Close chat"
            >
              ✕
            </button>
          </div>

          {/* INTERNAL SCROLL AREA */}
          <div
            className="p-3 sm:p-4 overflow-y-auto"
            style={{ maxHeight: 'calc(100dvh - 11.5rem)' }}
          >
            <Chat />
          </div>
        </div>
      )}
    </>
  );
}
