"use client";

import React from "react";

type NavButtonProps = {
    label: string;
    targetId?: string;
    isActive?: boolean;
};

export function NavButton({label, targetId, isActive = false,}: NavButtonProps) {
    function handleClick() {
        if (!targetId) return;
        document.getElementById(targetId)?.scrollIntoView({
            behavior: "smooth",
            block: "start",
        });
    }

    return (
        <button
            type="button"
            onClick={handleClick}
            className={`nav-link cursor-pointer ${isActive ? "is-active" : ""}`}
        >
            {label}
        </button>
    );
}
