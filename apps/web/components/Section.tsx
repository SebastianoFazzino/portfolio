import React from "react";

export function Section({
                            children,
                        }: {
    children: React.ReactNode;
}) {
    return (
        <section className="py-24">
            <div className="max-w-5xl mx-auto px-6">
                {children}
            </div>
        </section>
    );
}
