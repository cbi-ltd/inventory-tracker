# FuelFlow: receiver for the CAMS payment webhook (contract v1)

Files are laid out as in the FuelFlow repository, so they can be copied over it. `ALL.diff` is the same change as a patch.
Nothing here has been applied to your repository. Review the diff first (FuelFlow's own rules ask for that).

## What was wrong (found by reading the code)

The CAMS payment path could not have worked end to end. There were three independent reasons:

1. **The webhook routes were closed.** They were not in `permitAll`, and `CamsAuthenticationFilter` answers 401 to any call without a user token, so CAMS could never reach the controller.
2. **`Payment.paymentTime` is `NOT NULL`, and the CAMS payment builders never set it.** The insert would fail and roll the payment back.
3. **The card flow used its pending note twice.** The webhook service consumed (deleted) it, then `createCardPaymentFromCamsNotification` consumed it again, which always threw and rolled the payment back.

Also fixed: "not ours" and duplicate notices returned a 500 (so CAMS would retry them for 24 hours); a transfer matched the oldest pending sale on the account with no amount check; the signature was computed over a decoded string, not the exact bytes; a paid sale with too little recorded stock rolled its payment back; cancelling a sale left its pending note behind; the POS endpoint accepted a database entity as its request body.

## What the patch does

| Area | Change |
| --- | --- |
| Routes | `POST /api/v1/integrations/cams/transfer` and `/card` (same paths) are open in `SecurityConfig` and `CamsAuthenticationFilter`. The signature is their only gate. |
| Signature | `X-Timestamp` + `X-Signature`: HMAC-SHA256 of `{timestamp}.{exact body bytes}`, constant-time compare, 5-minute window, current and previous secret (for rotation). Blank secrets can never validate. |
| Replies | `{"status": "processed" / "duplicate" / "ignored", "message": ...}`. 401 bad signature or timestamp, 400 unreadable body, 500 only for a real failure. |
| Idempotency | New table `cams_webhook_events` (unique `eventId`). The raw body is kept only for notices that matched nothing or had the wrong amount. |
| Matching | By **terminal id** and **exact amount** among the terminal's open, unexpired payments (oldest first). The notice's `institutionId` must be FuelFlow's. |
| Wrong amount | Recorded as a payment with status `RECONCILIATION_PENDING` on the waiting sale. The sale is not completed. |
| Pending notes | Expire after 15 minutes (transfer) or 10 (card), measured from `createdAt`. One open payment per terminal and method. Re-registering a sale replaces its note. Cancelling a sale clears it. |
| Payments | `paymentTime` is set. The card note is deleted once. If completing the sale fails (for example not enough stock), the payment stays and the sale is flagged `RECONCILIATION_PENDING`. |
| Times | CAMS sends UTC (`...Z`). They are read as instants and converted to the server's local time. |

## Settings (`application.properties`)

| Key | Environment variable | Meaning |
| --- | --- | --- |
| `cams.webhook.secret` | `CAMS_WEBHOOK_SECRET` (as before) | Must equal CAMS's `PARTNER_WEBHOOK_SECRET_FUELFLOW` |
| `cams.webhook.previous-secret` | `CAMS_WEBHOOK_PREVIOUS_SECRET` | Old secret, only while rotating |
| `cams.webhook.tolerance-seconds` | | Default 300 |
| `cams.institution-id` | `CAMS_INSTITUTION_ID` | FuelFlow's institution id on CAMS. Empty = not checked (a warning is logged at startup). |
| `cams.pending.transfer-ttl-minutes` / `card-ttl-minutes` | | Defaults 15 and 10 |
| `cams.pending.single-open-per-terminal` | | Default `true`. Set `false` to allow several open payments on a terminal. |

## Things to confirm before deploying

1. **New table.** `cams_webhook_events` is created by `ddl-auto=update` on the first start. No migration file was touched.
2. **Behaviour change at the POS.** While a terminal has an open payment of a method, creating a second sale with that method on it now returns **409** until the first is paid, cancelled or expires. If that is too strict, set `cams.pending.single-open-per-terminal=false` (matching is then by amount only).
3. **Terminal id.** CAMS sends the CAMS terminal id (`2CBI` plus 4 characters). FuelFlow's `Terminal.terminalSerialNumber` must hold that same value, not the hardware serial number. Check your existing terminal records.
4. **Not enforced yet:** `merchantProfileId` is received but not checked, because FuelFlow stations do not yet carry a CAMS merchant link.
5. **`/api/v1/pending-transfer`** now takes `RegisterPendingTransferRequest` (same URL and JSON fields). Extra fields such as `id` are ignored.
6. **Secrets.** The `.env` in the zip you shared holds real-looking values (database, webhook secret, mail). Rotate them.

## Trying it by hand (works against a running FuelFlow)

```bash
SECRET='<CAMS_WEBHOOK_SECRET>'
TS=$(date +%s)
BODY='{"version":"1","eventId":"evt_test_1","eventType":"payment.success","requestReference":"REF_TEST_1","institutionId":"<institution id>","deviceSerial":"<terminal id>","amount":5000.00,"paymentMethod":"TRANSFER","paymentTime":"2026-11-05T10:00:00Z"}'
SIG=$(printf '%s.%s' "$TS" "$BODY" | openssl dgst -sha256 -hmac "$SECRET" -hex | sed 's/^.* //')
curl -i -X POST https://<fuelflow-host>/api/v1/integrations/cams/transfer \
  -H "Content-Type: application/json" -H "X-Timestamp: $TS" -H "X-Signature: $SIG" --data "$BODY"
```

Expect `200 {"status":"ignored",...}` if no sale is waiting on that terminal, `200 processed` if one is waiting for exactly that amount,
`401` if you change one character of the body after signing, and `200 duplicate` if you send the same event twice.
This recipe reproduces the contract's test vector (`3a34ddbe...b0ed`).

## What was verified here, and what was not

Run for real (JDK, no Spring needed): the signature check, the matcher and the event-id helper, with **24 JUnit 5 tests** in `src/test`
(`mvn test -Dtest='WebhookSignatureTest,PendingMatcherTest,EventIdsTest'`). They include the contract's signing test vector, which the Go sender
also reproduces. Eleven deliberate bugs were introduced one at a time and every one was caught.

Also run for real, outside the repository: the **actual source** of `CamsWebhookService` (23 scenarios) and of `PendingTransferService` and
`PendingCardPaymentService` (18 checks), with the Lombok annotations expanded by hand and simple recording fakes in place of the database and
the payment service. Nine deliberate bugs in the webhook service were all caught. Those scenarios are not in the repository as tests.

**Not done:** FuelFlow was not built with Maven (Maven Central and the Spring, Lombok and JPA libraries were not available), so the Spring wiring
itself (controller mapping, security rules, JPA queries and the new entity) has not been compiled or started. Every getter, setter and builder
call was checked against the real field declarations, every project method the new code calls was confirmed to exist, and all files were
syntax-checked. Run `mvn compile` and `mvn test` first. Also not done: an end-to-end run against the CAMS sender.
