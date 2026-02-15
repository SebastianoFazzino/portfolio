import './globals.css';
import './nav.css';
import React from 'react';
import { Header } from '@/components/common/Header';
import { BackendHeartbeat } from '@/components/utils/BackendHearthBeat';
import { ChatLauncher } from '@/components/chat/ChatLauncher';
import type { Metadata } from 'next';
import Script from 'next/script';

export const metadata: Metadata = {
  metadataBase: new URL('https://sebastianofazzino.com'),
  title: {
    default: 'Sebastiano Fazzino — Software Engineer',
    template: '%s — Sebastiano Fazzino',
  },
  description:
    'Full-stack software engineer based in Tallinn building reliable, production-grade systems across backend and frontend.',
  openGraph: {
    title: 'Sebastiano Fazzino — Software Engineer',
    description:
      'Full-stack engineer focused on reliable systems and production-grade architecture.',
    url: '/',
    siteName: 'Sebastiano Fazzino',
    type: 'website',
  },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className="dark">
      <body>
        {/* Structured data for search engines describing this site */}
        <Script
          id="ld-person"
          type="application/ld+json"
          strategy="beforeInteractive"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify({
              '@context': 'https://schema.org',
              '@type': 'Person',
              name: 'Sebastiano Fazzino',
              jobTitle: 'Software Engineer',
              url: 'https://sebastianofazzino.com',
              address: {
                '@type': 'PostalAddress',
                addressLocality: 'Tallinn',
                addressCountry: 'EE',
              },
              sameAs: [
                'https://github.com/SebastianoFazzino',
                'https://www.linkedin.com/in/sebastiano-fazzino/',
              ],
            }),
          }}
        />

        {/* Keep backend awake while the user is active */}
        <BackendHeartbeat />

        <div className="min-h-screen flex flex-col">
          <Header />
          <main className="flex-1">{children}</main>
          <ChatLauncher />

          <footer className="border-t border-white/10">
            <div className="max-w-5xl mx-auto px-6 py-8 text-sm text-white/50">
              © {new Date().getFullYear()} — Built & hosted on my home server.
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
