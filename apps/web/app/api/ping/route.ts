import {NextResponse} from "next/server";
import {postToBackend} from "@/app/api/_lib/backend-client";

export const runtime = "nodejs";

/**
 * POST /api/ping
 *
 * Lightweight health / warm-up endpoint.
 * Used to keep backend services responsive while a visitor is active.
 */
export async function POST() {
    try {
        const backendResponse = await postToBackend({
            path: "/ping",
            body: {},
        });

        if (backendResponse.ok) {
            return NextResponse.json({ ok: true }, { status: 200 });
        }

        return NextResponse.json({ ok: false }, { status: 502 });

    } catch (error) {
        console.error("[ping] Backend unreachable", { error });
        return NextResponse.json({ ok: false }, { status: 502 });
    }
}
