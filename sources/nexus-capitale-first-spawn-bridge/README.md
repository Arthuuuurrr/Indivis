# Nexus Capitale First Spawn Bridge 1.0.1

Server-side Fabric 1.21.11 compatibility bridge.

## Cause confirmée

La 1.0.0 détectait correctement la création d'un nouveau vault Nexus et téléportait bien le joueur
sur le dirigeable, mais elle appelait l'ancien wrapper RP-HUD :

`capitale:spawn/character_first_join_to_prologue_start_self`

Ce wrapper ajoute `capitale_identity_mandatory`, ce qui réouvre l'ancien menu de création
d'identité RP-HUD alors que NexusCharacters a déjà créé le personnage avant la connexion.

## 1.0.1

Le bridge conserve exactement le même déclencheur fiable :
`ServerAuthorityV080.createEmptyAuthoritativeVault(Path, UUID)`.

À la connexion du propriétaire, il attend 3 ticks, purge les anciens marqueurs d'identité RP-HUD
(`capitale_identity_mandatory`, `capitale_identity_open_pending`,
`capitale_identity_menu_opened`) puis exécute directement :

`function capitale:spawn/first_join_to_prologue_start_self`

Cette fonction conserve :
- `player/init`;
- l'initialisation des scores;
- le TP/spawnpoint du dirigeable;
- le démarrage de `QUEST_SPAWN` / `QUEST_PROLOGUE`;

mais n'appelle pas l'ancien menu d'identité.

Les personnages existants ne passent pas par ce hook.

Aucune modification de NexusCharacters PRE9, Creature Bundle, EasyNPC, quêtes ou logique Orc.
