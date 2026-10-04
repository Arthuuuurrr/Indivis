# Indivis NPC Restore 1.0.2

Minecraft 1.21.11 / EasyNPC 7.12.1.

Cette version remplace la stratégie 1.0.1 pour les restaurations réelles.

## Diagnostic confirmé

Le dossier EasyNPC fourni le 01/10/2026 contient 370 fichiers NPC pour 370 entrées d'index.
L'index migré stocke encore les dimensions sous la forme legacy `ResourceKey[minecraft:dimension / ...]`.

La commande simple `easy_npc spawn <uuid>` peut donc échouer avant la restauration lorsqu'elle tente de résoudre la dimension.
La surcharge `easy_npc spawn <uuid> <x> <y> <z>` contourne ce champ : le datapack exécute la commande dans la dimension normalisée et avec la position sauvegardée.

## Commandes

- `/function indivis_npc_restore:respawn_prologue`
  - Géraud Rivet — `fd804b49-4ba6-4565-86b3-8fab9d129c7b` — 765.5 299 1105.5
  - Pipin Soupape — `c84e33cb-70fe-4266-a7b0-1ac79fda04cb` — 765.5 290 1129.5
  - Jojo des Haubans — `a132bc70-cb9b-4563-9de2-a08ed8b57689` — 762.5 297 1135.5
  - Fernand Trois-Dents — `3111e0ea-dca3-4067-bcdf-fe39b296657f` — 766.5 301 1146.5
  - Althéon Brumeforge — `11cec64e-b3b2-40c4-8462-68d5a4107dcb` — 766.5 297 1152.5

- `/function indivis_npc_restore:respawn_missing_saved`
  - traite les 190 entrées dont `RemovalReason=NONE`;
  - ignore les entités déjà chargées avec le même UUID;
  - n'impose pas de restauration aux 173 `UNLOADED_TO_CHUNK` ni aux 7 `KILLED`;
  - une entrée est traitée par fonction planifiée, donc une erreur ne bloque pas les suivantes.

SHA-256 du ZIP livré : `7de5999109b4f76301ff9e08b97d31e4667588100096bb28519dd02c9bc40df4`.
