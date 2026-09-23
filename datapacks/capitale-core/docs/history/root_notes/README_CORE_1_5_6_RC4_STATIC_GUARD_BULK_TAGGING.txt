CAPITALE CORE 1.5.6 RC4 — Static guard bulk tagging

Base : 1.5.6 RC3 static_guard_anchor_admin

Ajouts :
- Menu admin /function capitale:admin/static_guards/menu_self
- Tag/ancrage du PNJ EasyNPC le plus proche sans ouvrir sa configuration
- Retrait du tag/ancre du PNJ proche
- Recréation groupée des ancres pour PNJ déjà tagués et chargés
- Tag d'entity type optionnel #capitale:static_guard_npc_candidates
- Documentation docs/GUIDE_GARDES_STATIQUES_BULK_TAGGING_1_5_6_RC4.txt

Rappel : ce système simplifie l'ancrage, mais ne remplace pas l'action EasyNPC On Hurt executeAsUser=false nécessaire au déclenchement du timer de retour.
