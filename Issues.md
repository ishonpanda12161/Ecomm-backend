# Code Review — Open Issues

> Fresh pass. All reported issues are resolved. Only ignored items remain.
> Status: 🔵 ACCEPTED (ignored per request)


====================================================================
## Ignored (per your request — not addressed)
====================================================================
- **C3** — Hardcoded JWT secret left in `application.properties` for local testing.
- **M3** — CORS policy not configured yet; deferred.
- **L4** — Production config hygiene (`ddl-auto=update`, plaintext DB password,
  mapper gaps) deferred.
- **H6** — JWT cookie `secure=false` left as-is for local dev.

====================================================================
## Optional follow-ups (not blocking)
====================================================================
- Deactivated products still appear in storefront listings (queries don't filter
  `isActive`). Add `@SQLRestriction("isActive = true")` to `Product` to auto-filter.
- `OrderItem` stores only `price`+`discount`, not `specialPrice`/line total — the
  order total isn't directly recomputable from its lines. Optional for a learning
  project.
