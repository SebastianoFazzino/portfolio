'use client';

import React from 'react';
import { NavButton } from '@/components/common/NavButton';

const sectionIds = ['about', 'experience', 'certifications', 'projects', 'connect'] as const;

function scrollToTop() {
  const currentScrollY = window.scrollY;

  if (window.location.hash) {
    window.location.hash = '';
    window.scrollTo({ top: currentScrollY, behavior: 'auto' });
  }

  requestAnimationFrame(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
}

export function Header() {
  const [activeSectionId, setActiveSectionId] = React.useState<string | null>(null);
  const [mobileOpen, setMobileOpen] = React.useState(false);

  const menuPanelRef = React.useRef<HTMLDivElement | null>(null);
  const menuToggleRef = React.useRef<HTMLButtonElement | null>(null);

  React.useEffect(() => {
    const sectionElements = sectionIds
      .map((id) => document.getElementById(id))
      .filter((element): element is HTMLElement => Boolean(element));

    if (sectionElements.length === 0) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const visibleEntries = entries
          .filter((entry) => entry.isIntersecting)
          .sort((a, b) => (b.intersectionRatio ?? 0) - (a.intersectionRatio ?? 0));

        if (visibleEntries.length > 0) {
          setActiveSectionId(visibleEntries[0].target.id);
        } else {
          setActiveSectionId(null);
        }
      },
      {
        root: null,
        rootMargin: '-30% 0px -60% 0px',
        threshold: [0.1, 0.2, 0.35, 0.5, 0.65],
      },
    );

    for (const element of sectionElements) observer.observe(element);
    return () => observer.disconnect();
  }, []);

  // Close menu on scroll or click (only when open)
  React.useEffect(() => {
    if (!mobileOpen) return;

    const close = () => setMobileOpen(false);

    const onDocumentClick = (e: MouseEvent) => {
      const target = e.target as Node | null;

      // If click is inside the menu panel or the toggle button -> do nothing
      if (target && menuPanelRef.current?.contains(target)) return;
      if (target && menuToggleRef.current?.contains(target)) return;

      close();
    };

    const onScroll = () => close();

    window.addEventListener('scroll', onScroll, { passive: true });
    document.addEventListener('click', onDocumentClick);

    return () => {
      window.removeEventListener('scroll', onScroll);
      document.removeEventListener('click', onDocumentClick);
    };
  }, [mobileOpen]);

  return (
    <header className="sticky top-0 z-200 h-16 border-b border-white/10 bg-black/60 backdrop-blur">
      <div className="max-w-5xl mx-auto h-16 px-4 sm:px-6 flex items-center justify-between">
        <button
          onClick={() => {
            setMobileOpen(false);
            scrollToTop();
          }}
          className="text-[0.9rem] tracking-[0.15rem] uppercase text-white/60 hover:text-(--accent) cursor-pointer"
          aria-label="Scroll to top"
        >
          Sebastiano Fazzino
        </button>

        {/* Desktop nav */}
        <nav className="hidden md:flex gap-6 text-sm tracking-wide text-white/60">
          <NavButton label="About" targetId="about" isActive={activeSectionId === 'about'} />
          <NavButton
            label="Experience"
            targetId="experience"
            isActive={activeSectionId === 'experience'}
          />
          <NavButton
            label="Certifications"
            targetId="certifications"
            isActive={activeSectionId === 'certifications'}
          />
          <NavButton
            label="Projects"
            targetId="projects"
            isActive={activeSectionId === 'projects'}
          />
          <NavButton label="Connect" targetId="connect" isActive={activeSectionId === 'connect'} />
        </nav>

        {/* Mobile toggle */}
        <button
          ref={menuToggleRef}
          type="button"
          className="md:hidden inline-flex items-center justify-center rounded-md border border-white/10 px-3 py-2 text-sm text-white/70 hover:text-white hover:border-white/20"
          onClick={() => setMobileOpen((open) => !open)}
          aria-label={mobileOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={mobileOpen}
        >
          {mobileOpen ? 'x' : '...'}
        </button>
      </div>

      {/* Mobile dropdown */}
      {mobileOpen && (
        <div className="md:hidden fixed left-0 right-0 bottom-0 top-16 z-150">
          <button
            className="absolute inset-0 bg-black/80"
            aria-label="Close menu"
            onClick={() => setMobileOpen(false)}
          />

          {/* Dropdown panel */}
          <div
            ref={menuPanelRef}
            className="absolute left-0 right-0 top-0 border-b border-white/10 bg-black/90 backdrop-blur"
          >
            <div className="max-w-5xl mx-auto px-4 sm:px-6 py-4">
              <nav className="flex flex-col items-end text-right gap-2 text-base tracking-wide text-white/70">
                {/* Close menu AFTER the NavButton click runs */}
                <div className="w-full flex justify-end" onClick={() => setMobileOpen(false)}>
                  <NavButton
                    label="About"
                    targetId="about"
                    isActive={activeSectionId === 'about'}
                  />
                </div>
                <div className="w-full flex justify-end" onClick={() => setMobileOpen(false)}>
                  <NavButton
                    label="Experience"
                    targetId="experience"
                    isActive={activeSectionId === 'experience'}
                  />
                </div>
                <div className="w-full flex justify-end" onClick={() => setMobileOpen(false)}>
                  <NavButton
                    label="Certifications"
                    targetId="certifications"
                    isActive={activeSectionId === 'certifications'}
                  />
                </div>
                <div className="w-full flex justify-end" onClick={() => setMobileOpen(false)}>
                  <NavButton
                    label="Projects"
                    targetId="projects"
                    isActive={activeSectionId === 'projects'}
                  />
                </div>
                <div className="w-full flex justify-end" onClick={() => setMobileOpen(false)}>
                  <NavButton
                    label="Connect"
                    targetId="connect"
                    isActive={activeSectionId === 'connect'}
                  />
                </div>
              </nav>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
