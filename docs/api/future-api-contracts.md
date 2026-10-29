# Future API contracts

> **PROPOSED — NOT IMPLEMENTED OR VERIFIED.** No backend exists. These endpoints are a discussion draft so the Android repository interfaces can be shaped for a later swap from mock data to a real service. Nothing in the MVP calls the network.

Purpose: describe a candidate HTTP contract for catalog, generation, library and progress, including language negotiation.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §11, §11.1, §4.4 rule 13; `docs/design/LittleStory_Figma_Design_Details.md` (screens SCR-03…SCR-12); workbook tickets LS-140, LS-141; ADR-007 (localized mock content), ADR-014 (simulations).

## 1. Conventions

| Topic | Proposal | Open? |
| --- | --- | --- |
| Base URL | `https://api.<tbd>/v1` set per build type through `BuildConfig`, never in feature code | Host and environments TBD |
| Transport | HTTPS only; TLS 1.2+; no cleartext (`usesCleartextTraffic=false`) | – |
| Format | JSON, UTF-8, `camelCase` fields, ISO-8601 UTC timestamps, durations in seconds | – |
| Versioning | Path major version (`/v1`); additive changes only within a major; `Deprecation` + `Sunset` headers before removal | Support window TBD |
| IDs | Opaque strings, stable across languages (same story ID in `en` and `kn`) | – |
| Auth | `Authorization: Bearer <access token>` issued to a **parent** account; child profiles are sub-resources of that account, never separate logins | Identity provider TBD |
| Idempotency | `Idempotency-Key` header on every `POST` and `PUT` that creates or changes state | – |
| Request tracing | `X-Request-Id` echoed in responses and error bodies; never logged with child content | – |

## 2. Language negotiation

| Rule | Detail |
| --- | --- |
| Client signal | App sends `Accept-Language` from the app's selected language (`en` or `kn`), e.g. `Accept-Language: kn, en;q=0.5`. |
| Explicit override | Optional `locale` query/body field (BCP-47, e.g. `kn`) wins over the header. Used for "story language" (FR-21), which can differ from UI language. |
| Response | `Content-Language` header names the language actually served. Each localized object carries `language` and `isFallback`. |
| Fallback | Field-level fallback to `en` when a `kn` value is missing (mirrors ADR-007). Server never returns an empty string or a raw key. |
| Unsupported tag | Returns `en` with `isFallback: true`; never `406`. |
| Same IDs | Story, category and section IDs are identical in every language, so saved stories and progress survive a language change. |

## 3. Endpoints

### 3.1 `GET /v1/stories` — catalog (SCR-03, SCR-04a, SCR-05)

Query: `category`, `q` (search), `ageMin`, `ageMax`, `sort` (`recommended` | `shortest` | `newest`), `pageSize` (default 20, max 50), `pageToken`, `locale`.

```json
{
  "items": [
    {
      "id": "story_little_elephant",
      "title": "The Little Elephant Who Shared",
      "category": { "id": "friendship", "name": "Friendship" },
      "ageRange": { "min": 4, "max": 7 },
      "durationSeconds": 360,
      "artwork": { "palette": ["#F6B995", "#694B91"], "motif": "elephant" },
      "language": "en",
      "isFallback": false,
      "availableLanguages": ["en", "kn"]
    }
  ],
  "nextPageToken": "eyJvIjoyMH0",
  "totalCount": 18
}
```

Pagination: cursor-based (`pageToken`/`nextPageToken`); `totalCount` optional and may be approximate. Paging 3 on Android only if catalog size warrants it (brief §11.3).

### 3.2 `GET /v1/stories/{id}` — details and content (SCR-06, SCR-10)

```json
{
  "id": "story_little_elephant",
  "title": "ಹಂಚಿಕೊಂಡ ಪುಟ್ಟ ಆನೆ",
  "synopsis": "…",
  "lesson": "…",
  "sections": [ { "id": "s1", "order": 1, "text": "…" } ],
  "narration": { "available": false },
  "language": "kn",
  "isFallback": false
}
```

`narration.available` stays `false` until a real TTS/audio service is approved; the MVP player is a simulation (ADR-014, NFR-12).

### 3.3 `POST /v1/story-generations` — submit a generation job (SCR-07 → SCR-08)

```json
{
  "childProfileId": "child_01",
  "prompt": "Aarav helped his friend at school and played cricket in the evening.",
  "theme": "adventure",
  "mood": "magical",
  "durationMinutes": 8,
  "locale": "kn"
}
```

Response `202 Accepted`, `Location: /v1/story-generations/gen_8f2c`:

```json
{ "id": "gen_8f2c", "status": "queued", "createdAt": "2026-10-29T17:00:00Z", "pollAfterSeconds": 2 }
```

Validation errors return `422` with field codes (e.g. `prompt.tooLong`, `prompt.empty`) that the app maps to string resources — never English text shown directly. Limits mirror `AppConfig` (500 characters today).

### 3.4 `GET /v1/story-generations/{id}` — job status

```json
{
  "id": "gen_8f2c",
  "status": "succeeded",
  "stage": "preparing_narration",
  "progress": 1.0,
  "storyId": "story_gen_8f2c",
  "moderation": { "outcome": "passed" }
}
```

`status`: `queued` | `running` | `succeeded` | `failed` | `cancelled` | `rejected` (moderation). `stage` aligns with the three designed stages (imagining, writing, preparing narration). Cancellation: `DELETE /v1/story-generations/{id}` → `204` (see `ai-generation-flow.md`).

### 3.5 Saved stories and progress (SCR-12, FR-13, FR-14, FR-24)

| Method | Path | Body / response | Notes |
| --- | --- | --- | --- |
| `GET` | `/v1/me/saved-stories?childProfileId=&pageToken=` | list of `{ storyId, savedAt, source: "favourite" \| "created" }` | Scoped to the parent account |
| `PUT` | `/v1/me/saved-stories/{id}` | `{ "childProfileId": "child_01" }` → `200` | Idempotent; repeated saves never duplicate (NFR-11) |
| `DELETE` | `/v1/me/saved-stories/{id}?childProfileId=` | `204` | Idempotent |
| `PUT` | `/v1/me/reading-progress/{id}` | `{ "childProfileId": "child_01", "sectionId": "s3", "fraction": 0.42, "updatedAt": "…" }` | Last-writer-wins on `updatedAt`; conflict policy TBD |

## 4. Errors

All errors use one envelope (RFC 9457 problem-details shape):

```json
{
  "type": "https://api.<tbd>/errors/rate-limited",
  "code": "rate_limited",
  "status": 429,
  "requestId": "req_123",
  "retryAfterSeconds": 30,
  "fields": []
}
```

| Status | `code` | App behaviour (UI text comes from resources) |
| --- | --- | --- |
| 400 / 422 | `invalid_request`, field codes | Inline validation message |
| 401 | `unauthenticated` | Refresh token once, then sign-in flow (future) |
| 403 | `forbidden` | Generic error state |
| 404 | `not_found` | Empty/error state with back action |
| 409 | `conflict` | Re-fetch and retry once |
| 429 | `rate_limited` | Respect `Retry-After`; show friendly wait message |
| 451 / `rejected` job | `content_rejected` | Child-safe "let's try a different idea" message; no detail of the rejected content |
| 5xx | `server_error` | Retry with backoff, then error state with Retry |

## 5. Retry, timeouts, rate limits

| Topic | Proposal |
| --- | --- |
| Timeouts | connect 10 s, read 20 s for catalog; generation status polls are short requests, the job itself may run longer |
| Retry | Only idempotent requests (`GET`, `PUT`, `DELETE`, `POST` with `Idempotency-Key`); exponential backoff 1 s, 2 s, 4 s, max 3 attempts, jitter |
| Polling | Honour `pollAfterSeconds`; stop after a client cap (e.g. 120 s) and show the "paused" state already designed for SCR-08 |
| Rate limits | Per parent account and per device; generation has a stricter daily quota (cost control, see `ai-generation-flow.md`); `429` with `Retry-After` |
| Offline | Catalog and saved stories readable from local cache; writes queued only if offline sync is approved (brief §11.3) |

## 6. Unresolved backend decisions

| # | Question | Blocks |
| --- | --- | --- |
| Q1 | Identity provider and parental-consent flow (see `docs/security/child-safety-requirements.md`) | Any authenticated endpoint |
| Q2 | Hosting region and data residency (India DPDP considerations) | Launch |
| Q3 | Whether generated stories are stored server-side, and for how long | Saved stories, retention |
| Q4 | Real narration/TTS provider and Kannada voice quality | `narration.available` |
| Q5 | Subscription entitlements source (Play Billing vs server) | SCR-15 beyond demo |
| Q6 | Conflict policy for progress across devices | FR-24 multi-device |
| Q7 | Push vs polling for generation completion | Notifications (SCR-18) |
