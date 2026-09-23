Capitale Core 1.5.5 — hotfix profils RP hooks

Ajouts :
- /trigger InfoRP set 20 : registre des profils/personnages.
- fonctions de préparation pour futur mod capitale_profiles :
  - capitale:profiles/before_save_self
  - capitale:profiles/after_load_self
  - capitale:profiles/menu_self
  - capitale:profiles/rules_self
  - capitale:profiles/mod_hooks_self
- objectifs scoreboard : CAP_PROFILE_ACTIVE, CAP_PROFILE_LOCK, CAP_PROFILE_SWITCH, CAP_PROFILE_TMP.

Ce patch ne sauvegarde pas encore la playerdata complète. Il ajoute uniquement le socle RP/datapack et les points d’ancrage sûrs pour un futur mod serveur.

Base : capitale_core_beta_1_5_5_STABLE_hotfix_easynpc_conversations_only.zip
