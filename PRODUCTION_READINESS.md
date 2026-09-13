# Archivo Sonoro: production-readiness checklist

**Not ready for production.** This is a release work list, not a completed audit or a list of confirmed runtime defects. Work from P0 downward; do not treat passing unit tests as permission to deploy. See the [README](README.md) for architecture and local setup.

## Evidence and completion rules

- Code-inspected gaps: no supported first-admin bootstrap; activation tokens are returned for manual delivery rather than emailed; `images.texture` is ignored by the hardcoded stylesheet; brand contact/social data are placeholders.
- Fresh local rebrand verification (2026-09-13 local date, working tree over `ab6c3b4`, macOS): `npm --prefix frontend test -- --watch=false` passed 80 tests across 14 files; `npm --prefix frontend run build` passed. Re-run for each release candidate and record its revision and environment.
- Backend tests were not run in this rebrand session because Docker was unavailable. Browser E2E was not run. Historical backend/API totals are not fresh proof.
- Storage failure and concurrency items below are **hypotheses to reproduce**, not assertions that those failures currently occur.
- Check an item only with a linked test/log or reviewed artifact, candidate revision, environment and date. Redact credentials, tokens and personal data. A code inspection alone cannot close a runtime criterion.
- For each task, record an owner and evidence location before execution. Discovered defects need a reproduction, bounded fix, regression test and re-verification. Expand this checklist when the audit finds missing operations; coverage is not claimed complete.

## P0 — unblock a safe, reproducible demonstration

### Provisioning and activation

- [ ] Implement and document a supported first-admin bootstrap. Acceptance: works on an empty isolated database, grants the necessary permissions, refuses unsafe repeat use, uses no default password and exposes no public privilege-escalation endpoint.
- [ ] Prove bootstrap recovery and last-admin safety. Acceptance: repeat/concurrent attempts and interrupted provisioning cannot create uncontrolled privileged accounts or permanently lock out administration.
- [ ] Complete the subsequent-account activation workflow. Acceptance: decide and document secure manual delivery or implement email; prove activation, expiry, replay rejection, lost-link recovery and login without leaking tokens to logs, audit, screenshots or analytics.
- [ ] Provide opt-in, synthetic demo fixtures. Acceptance: one command/workflow creates documented role/permission cases, public content and sample files in disposable resources only; teardown never targets personal data.

### Isolated browser proof

- [ ] Establish a disposable PostgreSQL database/volume, separate writable storage and mail catcher with explicit environment settings. Acceptance: guard against the personal `banda` database, existing Compose volume, `.env` and `~/.music-band-app/files`; document safe teardown.
- [ ] Run real-browser guest, musician, limited-admin and fully authorized admin sessions. Acceptance: separate cookie stores; cover direct URLs, reload, navigation, login/logout, CSRF initialization and session expiry against the real API.
- [ ] Exercise the core journey end to end. Acceptance: bootstrap → create/activate musician → assign group → create event/collection → upload and grant a sheet → musician views/downloads → revoke access and confirm denial after refresh.
- [ ] Exercise global and explicit sheet-management scope independently. Acceptance: upload and edit succeed with intended scope; collection/recipient filtering never conceals valid choices or grants access beyond the actor's authority.
- [ ] Verify failure UX across admin screens. Acceptance: missing optional permissions do not report successful mutations as failures; required-load failures never masquerade as empty data; success messages appear only after confirmed success.
- [ ] Capture actionable failures. Acceptance: validation, unauthorized/forbidden, not-found, conflict, rate-limit, upload-limit and server/network failures show truthful messages, preserve useful input, stop spinners and offer safe recovery without duplicate writes.

## P1 — complete the role × operation audit

For **every row**, enumerate actual routes, endpoints and operations from code. Run applicable operations as guest, active musician, inactive/pending account, admin without permission, scoped admin and authorized admin. Include own/other group and resource cases, direct API requests (not just hidden buttons), positive and negative paths, stale sessions and permission revocation. Mark unsupported operations explicitly rather than inventing features.

| Module | Operations and boundary cases required before checking |
| --- | --- |
| Auth | CSRF, login/logout, activation, reset request/redemption; invalid/expired/reused tokens, account enumeration, rate limits, cookie flags and invalidated sessions |
| Users | Create/list/detail/edit/deactivate, role and admin-permission changes; duplicate email, self-target restrictions, last privileged admin, minors/guardian consent and stale versions |
| Groups | Create/list/detail/edit/delete, list/assign/unassign musicians; inactive users, duplicate membership, dependent resources and scope changes |
| Events | Public/private list/detail, create/edit/cancel, recipients and access grants; group/direct visibility, revoked membership, stale versions and date/time-zone rendering |
| Collections and sheets | Collection lifecycle; upload/list/detail/edit/delete/download and access grants; global/explicit scope, recipients, file validation, missing files and cross-resource identifiers |
| News | Public reads and each supported admin mutation; visibility, invalid content, absent records and conflicts |
| Gallery | Public albums/photos/file reads and admin album/photo lifecycle; upload limits/type, deletion dependencies, unsafe files and missing storage |
| Videos | Public reads and admin link lifecycle; malformed/unsafe URLs, embedding restrictions, visibility and conflicts |
| Courses | Public reads and admin announcement lifecycle; dates, validation, visibility and conflicts |
| Contact | Public submit and any supported admin operations; validation, spam/rate limits, SMTP outage, persistence versus notification outcome and privacy |
| Audit | Authorized list/filter/pagination; denied access, correct actor/action/resource, mutation coverage, redaction and rollback consistency |

- [ ] Auth audit complete with evidence for each applicable role/operation.
- [ ] Users and admin-permissions audit complete with evidence for each applicable role/operation.
- [ ] Groups and membership audit complete with evidence for each applicable role/operation.
- [ ] Events and event-access audit complete with evidence for each applicable role/operation.
- [ ] Collections, sheets and sheet-access audit complete with evidence for each applicable role/operation.
- [ ] News audit complete with evidence for each applicable role/operation.
- [ ] Gallery and photo-file audit complete with evidence for each applicable role/operation.
- [ ] Videos audit complete with evidence for each applicable role/operation.
- [ ] Courses audit complete with evidence for each applicable role/operation.
- [ ] Contact audit complete with evidence for each applicable role/operation.
- [ ] Audit-history audit complete with evidence for each applicable role/operation.

### Storage and concurrency: reproduce before classifying

- [ ] Inject unwritable/full storage, missing files and interrupted uploads in isolation. Acceptance: bounded truthful errors, no unauthorized file disclosure and documented cleanup/reconciliation for orphan metadata or files.
- [ ] Inject database rollback after file creation/deletion. Acceptance: prove the database/filesystem consistency policy and compensating recovery; retain repro tests for any confirmed defect.
- [ ] Race sheet/photo upload, edit, delete and download from separate sessions. Acceptance: no silent data loss, unintended cross-resource access or unmanaged orphan files; conflicts are recoverable.
- [ ] Race user/role/permission, membership, event and collection changes. Acceptance: uniqueness and last-admin rules survive concurrency; stale versions are rejected and UI refresh/retry preserves user intent.
- [ ] Race token redemption and permission revocation with requests already in flight. Acceptance: one-time token semantics and documented authorization timing hold under repeatable tests.
- [ ] Measure representative listings, downloads and upload memory use. Acceptance: agree dataset/file/concurrency limits, then meet measured latency and memory budgets or implement pagination/streaming where required.

### Security, privacy and usability

- [ ] Review server-side authorization and input/file validation independently. Acceptance: test cross-resource access, mass assignment, stored XSS, malicious URLs, path traversal and MIME/content mismatch; resolve critical/high findings.
- [ ] Verify proxy-aware abuse controls and request limits. Acceptance: spoofed forwarding headers cannot bypass limits; shared-IP and multi-instance behavior is documented and tested.
- [ ] Define personal-data, minor-consent, contact-message and audit retention/deletion policy. Acceptance: collection is necessary, access is restricted and deletion/export obligations have tested procedures.
- [ ] Perform keyboard, screen-reader, contrast and responsive-browser checks. Acceptance: critical journeys have visible focus, labeled controls, announced errors and usable layouts at agreed viewport sizes.
- [ ] Finish brand configuration. Acceptance: wire `images.texture` with a regression test, replace placeholder contact/social information only with approved data, and review titles, favicon, assets and missing-asset fallbacks.
- [ ] Review font/media licensing and external requests. Acceptance: approve content rights and privacy implications (including hosted fonts/video embeds); provide appropriate fallback behavior.

## P2 — deployment and operational release gates

- [ ] Choose and document hosting topology. Acceptance: reproducible backend/frontend artifacts, persistent database and file storage, least-privilege runtime users and no reliance on ephemeral application filesystems.
- [ ] Configure domain, HTTPS and reverse proxy. Acceptance: valid TLS renewal, HTTP redirect, secure cookies, same-origin `/api`, deep-link SPA fallback, upload/time limits and non-public database/mail administration ports are verified externally.
- [ ] Configure production secrets and environments. Acceptance: no development credentials/flags, startup fails for required missing values, secrets are injected securely, JWT/database/SMTP rotation and session invalidation are rehearsed.
- [ ] Configure real SMTP and public frontend origin. Acceptance: authenticated/TLS mail, authorized sender domain and deliverability are tested; reset links use the real HTTPS origin; outage alerts and retry/manual recovery policy are documented.
- [ ] Rehearse database migration and release rollback. Acceptance: empty install and upgrade from a representative backup pass Flyway validation; schema adoption is reviewed, not blindly baselined; app/schema compatibility and rollback limits are documented.
- [ ] Implement consistent database **and file** backups, encrypted off-site retention and restore drills. Acceptance: restore into a clean environment and verify accounts, permissions, file downloads and agreed recovery-time/data-loss objectives.
- [ ] Establish health/readiness checks and monitoring. Acceptance: probes distinguish liveness from unavailable dependencies, reveal no secrets, and trigger tested alerts for API failures, SMTP issues, disk capacity and backup failure.
- [ ] Define logging and incident response. Acceptance: correlation and audit evidence support diagnosis without passwords/tokens/PII leakage; operator runbooks cover revocation, failed deployment, storage repair and dependency outages.
- [ ] Add CI release gates. Acceptance: clean locked frontend install, tests/build, Java 21 backend suite with Docker/Testcontainers, isolated browser scenarios, migration checks, dependency/secret scanning and artifact provenance run on the candidate revision.
- [ ] Verify fresh backend and API results. Acceptance: record exact commands, environment and pass/fail output; no historical test counts substituted for this candidate's evidence.
- [ ] Review dependency/runtime versions and licenses. Acceptance: supported versions and actionable vulnerability findings are documented; upgrades are separate tested work, not cosmetic rebrand changes.
- [ ] Run a staging soak and capacity check. Acceptance: representative workflows survive restart, realistic load and planned outages within agreed budgets; all P0/P1 blockers are closed before production approval.

## P3 — repository and portfolio delivery

- [ ] Rename the GitHub repository to `archivo-sonoro` only with delivery authorization. Acceptance: confirm the new URL exists, update remote/docs/integrations and verify redirects; preserve old local storage/database identifiers unless separately migrated.
- [ ] Prepare honest portfolio evidence. Acceptance: approved synthetic screenshots/demo data, architecture narrative and tested setup; publish a demo URL only after the deployment actually exists and private data cannot be exposed.
- [ ] Review repository presentation and governance. Acceptance: description/topics, license decision, contribution/security-reporting guidance and branch protection match the intended public project.
- [ ] Refresh the Postman collection against current controllers and supported setup. Acceptance: remove obsolete permission/bootstrap claims and unsafe credential examples, document disposable environments, and verify requests without treating legacy notes as runtime evidence.
- [ ] Review and deliver changes through the agreed branch/PR workflow. Acceptance: focused diff, fresh checks and no secrets; commit/push/merge only when authorized, with a rollback boundary for each work unit.
- [ ] Obtain release approval and record residual risk. Acceptance: owner signs off on evidence for all release gates, any deferred non-blocker has rationale/owner/date, and rollback/incident ownership is explicit.
