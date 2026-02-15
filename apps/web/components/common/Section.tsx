import React from 'react';

export function Section({ id, children }: { id?: string; children: React.ReactNode }) {
  return (
    <section id={id} className="py-24 scroll-mt-24">
      <div className="max-w-5xl mx-auto px-6">{children}</div>
    </section>
  );
}
