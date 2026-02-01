"use client";

import React from "react";
import {NavButton} from "@/components/NavButton";

const sectionIds = ["about", "experience", "certifications", "this-site", "connect"] as const;

function scrollToTop() {
    const currentScrollY = window.scrollY;

    if (window.location.hash) {
        window.location.hash = "";
        window.scrollTo({ top: currentScrollY, behavior: "auto" });
    }

    requestAnimationFrame(() => {
        window.scrollTo({ top: 0, behavior: "smooth" });
    });
}

export function Header() {
    const [activeSectionId, setActiveSectionId] = React.useState<string | null>(null);

    React.useEffect(() => {
        const sectionElements = sectionIds
            .map((id) => document.getElementById(id))
            .filter((element): element is HTMLElement => Boolean(element));

        if (sectionElements.length === 0) return;

        const observer = new IntersectionObserver(
            (entries) => {
                // Pick the entry that is most visible
                const visibleEntries = entries
                    .filter((entry) => entry.isIntersecting)
                    .sort((a, b) => (b.intersectionRatio ?? 0) - (a.intersectionRatio ?? 0));

                if (visibleEntries.length > 0) {
                    setActiveSectionId(visibleEntries[0].target.id);
                } else {
                    // If nothing intersects, don’t highlight anything.
                    setActiveSectionId(null);
                }
            },
            {
                // This makes “active” align with what feels like the reading area
                root: null,
                rootMargin: "-30% 0px -60% 0px",
                threshold: [0.1, 0.2, 0.35, 0.5, 0.65],
            }
        );

        for (const element of sectionElements) observer.observe(element);

        return () => observer.disconnect();
    }, []);

    return (
        <header className="sticky top-0 z-50 border-b border-white/10 bg-black/60 backdrop-blur">
            <div className="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
                <button
                    onClick={scrollToTop}
                    className="text-[0.9rem] tracking-[0.15rem] uppercase text-white/60 hover:text-(--accent) cursor-pointer"
                    aria-label="Scroll to top"
                >
                    Sebastiano Fazzino
                </button>

                <nav className="flex gap-6 text-sm tracking-wide text-white/60">
                    <NavButton
                        label="About"
                        targetId="about"
                        isActive={activeSectionId === "about"}
                    />
                    <NavButton
                        label="Experience"
                        targetId="experience"
                        isActive={activeSectionId === "experience"}
                    />
                    <NavButton
                        label="Certifications"
                        targetId="certifications"
                        isActive={activeSectionId === "certifications"}
                    />
                    <NavButton
                        label="This site"
                        targetId="this-site"
                        isActive={activeSectionId === "this-site"}
                    />
                    <NavButton
                        label="Connect"
                        targetId="connect"
                        isActive={activeSectionId === "connect"}
                    />
                </nav>
            </div>
        </header>
    );
}
