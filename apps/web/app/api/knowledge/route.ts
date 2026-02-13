export const runtime = "nodejs";
export const dynamic = "force-dynamic";

import { getStreamFromBackend } from "@/app/api/_lib/backend-client";

export async function GET(request: Request) {
    const url = new URL(request.url);
    const question = (url.searchParams.get("question") ?? "").trim();

    if (!question) {
        return new Response("Missing question", { status: 400 });
    }

    let upstream: Response;
    try {
        upstream = await getStreamFromBackend({
            pathWithQuery: `/knowledge/ask/stream?question=${encodeURIComponent(question)}`,
        });
    } catch {
        return new Response("Backend unreachable", { status: 502 });
    }

    if (!upstream.ok) {
        const text = await upstream.text().catch(() => "");
        return new Response(text || "Upstream error", { status: upstream.status });
    }

    if (!upstream.body) {
        return new Response("Upstream missing body", { status: 502 });
    }

    return new Response(upstream.body, {
        status: 200,
        headers: {
            "Content-Type": "text/event-stream; charset=utf-8",
            "Cache-Control": "no-cache, no-transform",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",
        },
    });
}
