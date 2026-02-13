import { NextResponse } from "next/server";
import { postToBackend } from "@/app/api/_lib/backend-client";

export const runtime = "nodejs";

export async function POST() {
    try {
        const backendResponse = await postToBackend({
            path: "/ping",
            body: {},
        });

        if (backendResponse.ok) {
            return NextResponse.json({ ok: true }, { status: 200 });
        }

        return NextResponse.json(
            { ok: false, status: backendResponse.status },
            { status: backendResponse.status }
        );
    } catch (error) {
        console.error("[ping] Backend unreachable", { error });
        return NextResponse.json({ ok: false }, { status: 502 });
    }
}
