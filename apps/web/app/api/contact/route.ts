import {NextResponse} from "next/server";

export const runtime = "nodejs";

type ContactPayload = {
    name: string;
    email: string;
    message: string;
    website?: string;
};

const RATE_LIMIT_WINDOW_MILLISECONDS = 60_000;
const RATE_LIMIT_MAX_REQUESTS_PER_WINDOW = 5;

const rateLimitStateByIp = new Map<string, { requestCount: number; resetAt: number }>();

function getClientIpAddress(request: Request): string {
    const forwardedForHeader = request.headers.get("x-forwarded-for");
    if (forwardedForHeader) {
        return forwardedForHeader.split(",")[0].trim();
    }
    return "unknown";
}

function isRequestAllowedByRateLimit(clientIpAddress: string): { allowed: boolean } {
    const nowEpochMilliseconds = Date.now();
    const existingState = rateLimitStateByIp.get(clientIpAddress);

    // First request from this IP, or the window has expired: start a new window.
    if (!existingState || nowEpochMilliseconds > existingState.resetAt) {
        rateLimitStateByIp.set(clientIpAddress, {
            requestCount: 1,
            resetAt: nowEpochMilliseconds + RATE_LIMIT_WINDOW_MILLISECONDS,
        });
        return { allowed: true };
    }

    // Window still active: reject if we are at/above the limit.
    if (existingState.requestCount >= RATE_LIMIT_MAX_REQUESTS_PER_WINDOW) {
        return { allowed: false };
    }

    // Otherwise increment and allow.
    existingState.requestCount += 1;
    rateLimitStateByIp.set(clientIpAddress, existingState);
    return { allowed: true };
}

/**
 * Basic email format check.
 */
function looksLikeEmailAddress(value: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

/**
 * Sends a transactional email via Brevo.
 */
async function sendEmailViaBrevo(args: {
    brevoApiKey: string;
    toEmailAddress: string;
    fromEmailAddress: string;
    fromDisplayName: string;
    replyToEmailAddress: string;
    replyToDisplayName: string;
    subject: string;
    textBody: string;
}): Promise<{ ok: boolean }> {
    const responseFromBrevo = await fetch("https://api.brevo.com/v3/smtp/email", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "api-key": args.brevoApiKey,
        },
        body: JSON.stringify({
            sender: { name: args.fromDisplayName, email: args.fromEmailAddress },
            to: [{ email: args.toEmailAddress, name: args.fromDisplayName }],
            subject: args.subject,
            textContent: args.textBody,
            replyTo: { email: args.replyToEmailAddress, name: args.replyToDisplayName },
        }),
    });

    if (!responseFromBrevo.ok) {
        return { ok: false };
    }

    return { ok: true };
}

/**
 * POST /api/contact
 *
 * Receives contact form submissions, validates them, rate-limits them,
 * and sends the message to your inbox via Brevo.
 *
 * Security:
 * - Honeypot: blocks bots
 * - Rate limit: limits repeated abuse
 */
export async function POST(request: Request) {
    try {
        const payload = (await request.json()) as Partial<ContactPayload>;

        // Honeypot: if filled, treat as spam and pretend it succeeded.
        if (payload.website && payload.website.trim().length > 0) {
            return NextResponse.json({ ok: true }, { status: 200 });
        }

        const name = (payload.name ?? "").trim();
        const email = (payload.email ?? "").trim();
        const message = (payload.message ?? "").trim();

        if (!name || !email || !message) {
            return NextResponse.json({ error: "Missing fields." }, { status: 400 });
        }

        if (!looksLikeEmailAddress(email)) {
            return NextResponse.json({ error: "Invalid email." }, { status: 400 });
        }

        if (message.length > 4000) {
            return NextResponse.json({ error: "Message too long." }, { status: 400 });
        }

        const clientIpAddress = getClientIpAddress(request);
        const rateLimitDecision = isRequestAllowedByRateLimit(clientIpAddress);

        if (!rateLimitDecision.allowed) {
            return NextResponse.json({ error: "Too many requests." }, { status: 429 });
        }

        const brevoApiKey = process.env.BREVO_API_KEY;
        const toEmailAddress = process.env.CONTACT_TO_EMAIL;
        const fromEmailAddress = process.env.CONTACT_FROM_EMAIL;
        const fromDisplayName = process.env.CONTACT_FROM_NAME ?? "Portfolio";

        if (!brevoApiKey || !toEmailAddress || !fromEmailAddress) {
            return NextResponse.json({ error: "Server not configured." }, { status: 500 });
        }

        const sendResult = await sendEmailViaBrevo({
            brevoApiKey,
            toEmailAddress,
            fromEmailAddress,
            fromDisplayName,
            replyToEmailAddress: email,
            replyToDisplayName: name,
            subject: `Portfolio contact: ${name}`,
            textBody: [`Name: ${name}`, `Email: ${email}`, "", message].join("\n"),
        });

        if (!sendResult.ok) {
            console.error("[contact] Failed to send email via Brevo", { clientIpAddress });
            return NextResponse.json({ error: "Failed to send." }, { status: 502 });
        }

        console.info("[contact] Message sent", { clientIpAddress });
        return NextResponse.json({ ok: true }, { status: 200 });
    } catch (error) {
        console.error("[contact] Bad request", { error });
        return NextResponse.json({ error: "Bad request." }, { status: 400 });
    }
}
