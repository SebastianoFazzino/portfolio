"use client";

import {useEffect, useRef, useState} from "react";
import {ContactForm} from "@/components/contact/ContactForm";

export function ContactModal() {
    const [isOpen, setIsOpen] = useState(false);
    const dialogRef = useRef<HTMLDialogElement | null>(null);

    function openModal() {
        setIsOpen(true);
    }

    function closeModal() {
        setIsOpen(false);
    }

    useEffect(() => {
        const dialog = dialogRef.current;
        if (!dialog) return;

        if (isOpen && !dialog.open) dialog.showModal();
        if (!isOpen && dialog.open) dialog.close();
    }, [isOpen]);

    useEffect(() => {
        const dialog = dialogRef.current;
        if (!dialog) return;

        function handleClose() {
            setIsOpen(false);
        }

        dialog.addEventListener("close", handleClose);
        return () => dialog.removeEventListener("close", handleClose);
    }, []);

    return (
        <>
            <button
                type="button"
                onClick={openModal}
                className="group inline-flex items-center gap-2 cursor-pointer text-white/60 hover:text-(--accent) transition-colors"
            >
                <span className="text-white/40 group-hover:text-(--accent)">→</span>
                <span className="underline underline-offset-4 decoration-white/20 group-hover:decoration-(--accent)">
                    Send a message
                </span>
            </button>

            { isOpen && (
                <dialog
                    ref={dialogRef}
                    onClick={(event) => {
                        if (event.target === event.currentTarget) closeModal();
                    }}
                    className="fixed inset-0 m-auto w-full max-w-2xl rounded-lg bg-[#0b0b0b] text-white border border-white/10 p-0 backdrop:bg-black/70"
                >
                    <div className="p-6 sm:p-8">
                        <div className="flex items-start justify-between gap-6">
                            <div>
                                <p className="mt-2 text-sm text-white/50">
                                    Send a message. It goes directly to my inbox.
                                </p>
                            </div>

                            <button
                                type="button"
                                onClick={closeModal}
                                className="text-sm cursor-pointer text-white/50 hover:text-rose-500"
                                aria-label="Close"
                            >
                                Close
                            </button>
                        </div>

                        <div className="mt-6">
                            <ContactForm onSentAction={() => setIsOpen(false)} />
                        </div>
                    </div>
                </dialog>
            )}
        </>
    );
}
