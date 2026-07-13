# Issues

Review of the ecommerce backend. IDs are renumbered fresh each pass (C = Critical, H = High, M = Medium, L = Low).
Items explicitly deferred/accepted by the user are listed under **Ignored / Accepted** at the bottom.

---

## ~~C1 — JWT secret was standard base64, decoded as BASE64URL → signin 500~~ (RESOLVED)
**Severity:** Critical — *fixed by user*
**Root cause (historical):** `jwt-secret` was standard base64 (`+`/`=`), but `key()` did `Decoders.BASE64URL.decode(jwtSecret)`, which throws `io.jsonwebtoken.io.DecodingException: Illegal base64url character: '+'`. That 500'd `POST /api/open/signin` (and every authenticated request via `valid()`/`extractUsername()`).
**Status:** User changed the `jwt-secret` to a valid base64url-safe value; signin now works. No code change needed if the secret stays base64url-safe. (If you ever use a non-base64 secret again, switch `key()` to `Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))`.)

---

## C2 — Token errors are never caught → 500 instead of 401 on authenticated requests
**Severity:** Critical
**Location:** `src/main/java/com/ecom/backend/security/jwt/JwtUtils.java:78-86` (`valid()`) and `src/main/java/com/ecom/backend/filters/JwtFilter.java:41-47`
**Symptom:** Any request carrying an **expired, malformed, or tampered** JWT returns `500` instead of a clean `401`. **Now live** — with C1 fixed, this fires on every authenticated request once the token expires or is tampered with.
**Root cause:** Both files import the project's *custom* exception
```java
import com.ecom.backend.exceptions.JwtException;
```
and `catch (JwtException e)`. jjwt throws its own `io.jsonwebtoken.JwtException` (and subtypes `ExpiredJwtException`, `SignatureException`, `MalformedJwtException`), which are **not** subclasses of the custom exception. So:
- `JwtUtils.valid()` lets the jjwt exception propagate instead of returning `false`.
- `JwtFilter` re-throws the custom `JwtException` (a RuntimeException) on a token that was already invalid.
Result: a bad token reaches the container as an uncaught exception → 500.
**Fix:**
- In `valid()`, catch the jjwt exception (or `Exception`) and return `false`:
  ```java
  public boolean valid(String token) {
      try {
          Date expiry = Jwts.parser().verifyWith(key()).build()
                  .parseSignedClaims(token).getPayload().getExpiration();
          return expiry.after(new Date());
      } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
          return false;
      }
  }
  ```
- In `JwtFilter`, when `valid(token)` is `false` (not just on exception), reject the request, e.g. `if (!jwtUtils.valid(token)) { filterChain.doFilter(...); return; }` or return `401`. Also wrap `extractUsername(token)` since it can throw jjwt exceptions too.

---

## H1 — JWT secret hardcoded in source (ignored by user for local testing)
See **Ignored / Accepted** below.

## H2 — JWT cookie `secure=false` (accepted for local dev)
See **Ignored / Accepted** below.

---

## M1 — No CORS configuration
**Severity:** Medium
**Location:** whole app (no `@CrossOrigin` / `WebMvcConfigurer` / `CorsConfigurationSource`)
**Symptom:** A browser front-end on a different origin (e.g. `localhost:3000`) calling `/api/**` will be blocked by the browser.
**Fix:** add a `CorsConfigurationSource` bean (or `@CrossOrigin` on controllers) allowing the front-end origin.
**Status:** deferred by user.

## M2 — No global exception handler; filter exceptions return HTML 500
**Severity:** Medium
**Location:** no `@RestControllerAdvice`; `JwtFilter` throws inside the filter chain
**Symptom:** Exceptions thrown in `JwtFilter` (incl. those from C2) produce Spring's default HTML error page instead of a JSON API error, and give `500` rather than a meaningful `401`/`403`.
**Fix:** add a `@RestControllerAdvice` translating `JwtException`/auth failures to JSON `401`, and resolve C2 so bad tokens don't throw.

## M3 — Sensitive config / DB credentials in `application.properties`
See **Ignored / Accepted** below.

---

## L1 — H2 console enabled alongside Postgres datasource
**Severity:** Low
**Location:** `application.properties` (`spring.h2.console.enabled=true` while `spring.datasource.url=jdbc:postgresql://...`)
**Symptom:** Confusing leftover config; H2 console has no matching H2 datasource. Harmless but should be cleaned up.
**Fix:** remove the H2 console flag (or the unused H2 dependency) when running on Postgres.

## L2 — JWT cookie `path("/api")` only
**Severity:** Low
**Location:** `JwtUtils.generateJwtCookie` / `cleanCookie` set `.path("/api")`
**Symptom:** The auth cookie is only sent to `/api/**`. Fine as long as every secured endpoint is under `/api`; double-check no secured route lives outside that path.
**Fix:** confirm all secured endpoints are under `/api`, or widen the path if needed.

---

## Previously fixed (not repeated as open)
- `UserResponseDTO` / `AuthController`: `roles` typed as `Collection<? extends GrantedAuthority>`; removed the bad `(Set<Role>)` cast and unused `Role` import (compile error resolved).
- `JwtFilter`: no longer throws when the request has no token (returns early, proceeds anonymously).
- Product delete now removes linked `CartItems` (H2 from earlier pass).

---

## Ignored / Accepted (per user decision — not to be fixed now)
- **H1 — Hardcoded JWT secret** in `application.properties` (kept for local testing).
- **H2 — JWT cookie `secure=false`** (acceptable over HTTP / local dev; must be `true` behind HTTPS in prod).
- **M1 — CORS not configured** (deferred).
- **M3 — Config hygiene**: DB credentials and secrets committed in `application.properties` (deferred; move to env vars / secret manager for prod).
