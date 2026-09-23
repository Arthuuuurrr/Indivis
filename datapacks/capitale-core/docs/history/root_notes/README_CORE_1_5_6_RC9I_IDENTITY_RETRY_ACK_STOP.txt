# Capitale Core 1.5.6 RC9i — Identity retry ack stop

Base: RC9h first-character identity bootstrap.

RC9i keeps the RC9h delayed identity opening, but fixes one issue found during static review:
- RC9h retried /capidentity menu until timeout even if the HUD had already validated the identity.
- RC9i stops the retry as soon as the player has the HUD tag `capitale.identity.created`.
- It also removes `capitale_identity_mandatory` after successful validation, avoiding accidental rearming by prologue functions.

No quest values, race values, health values, skin sync, dialogue flow, or patrol/vendor systems were changed.
