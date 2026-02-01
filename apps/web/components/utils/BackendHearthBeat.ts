"use client";

import {useEffect} from "react";

export function BackendHeartbeat() {
    useEffect(() => {
        let timer: ReturnType<typeof setInterval> | null = null;

        async function heartbeat() {
            if (document.visibilityState !== "visible") return;

            try {
                await fetch("/api/ping", { method: "POST" });
            } catch {}
        }

        // First contact as soon as the app loads
        void heartbeat();

        // Keep backend responsive while the session is active
        timer = setInterval(heartbeat, 5 * 60 * 1000);

        const onVisibilityChange = () => heartbeat();
        document.addEventListener("visibilitychange", onVisibilityChange);

        return () => {
            if (timer) clearInterval(timer);
            document.removeEventListener("visibilitychange", onVisibilityChange);
        };
    }, []);

    return null;
}
