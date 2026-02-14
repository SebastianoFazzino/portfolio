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
        const encoder = new TextEncoder();

        const stream = new ReadableStream({
            start(controller) {
                controller.enqueue(
                    encoder.encode(
                        `event: backend_error\ndata: ${JSON.stringify({
                            message: upstream.status === 429
                                ? "Too many requests" : "Upstream error",
                        })}\n\n`
                    )
                );
                controller.enqueue(encoder.encode(`event: done\ndata: {}\n\n`));
                controller.close();
            },
        });

        return new Response(stream, {
            status: 200,
            headers: {
                "Content-Type": "text/event-stream; charset=utf-8",
                "Cache-Control": "no-cache, no-transform",
                Connection: "keep-alive",
                "X-Accel-Buffering": "no",
            },
        });
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
