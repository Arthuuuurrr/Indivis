Haute Capitale Core — 1.5.6 RC9h FIRST CHARACTER / IDENTITY BOOTSTRAP

Base: recovered RC9g vendor near distance.

Targeted fixes:
- start_prologue_self no longer calls identity_open_mandatory_self before ensure_runtime + TP + spawnpoint.
- identity_open_mandatory_self now arms a temporary pending retry instead of adding capitale_identity_menu_opened immediately.
- New function: capitale:spawn/identity_open_retry_self.
- core/tick retries the identity menu once per second while tag capitale_identity_open_pending exists.
- Retry stops automatically after 40 attempts, so there is no permanent loop.

Why:
- On slow clients, the previous one-shot /capidentity menu could be sent before the player was fully loaded.
- The old function marked capitale_identity_menu_opened immediately, so no retry was possible.

New objectives:
- CAP_ID_OPEN_TRY
- CAP_ID_OPEN_CD

No quest progression is reset by this retry patch.
No validated HUD sync/race/skin values are changed here.
