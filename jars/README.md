# JAR serveur

Ce dossier contient les **JAR correspondant à l'état serveur de référence**, tous regroupés à plat dans un seul dossier.

Les binaires sont stockés avec **Git LFS**.

## Règles

- Tous les JAR actifs sont directement dans `jars/` : aucun sous-dossier par mod.
- Une seule version serveur de référence doit être conservée par module.
- Une version uniquement présente côté client ou dans les fichiers de travail n'est pas mise ici.
- Les copies Windows `(1)`, `(2)`, etc. ne sont pas considérées comme de nouvelles versions.
- Si le JAR serveur exact n'est pas disponible, il reste absent plutôt que d'être remplacé par une version approximative.

## JAR serveur actuellement archivés

- Arsenal — `PRIMARY-ORDER1`
- AzureLibArmor — `TEST3-RC1`
- capitale_admin_commands — `1.0.2 SERVER_ONLY_EVENT_ACCESS_FIX`
- capitale_armor_test — `0.7.3 local-player-first-person-only`
- capitale_currency — `BETA 1.0 hotfix5`
- capitale_housing — `BETA 0.1.0 HOTFIX2`
- capitale_rp_hud — `1.3.2 NEXUS_AUTHORITY_FINAL_ARMOR40`
- capitale_skills_items — `1.7.18 EVENT_ACCESS_FIX`
- capitale_weapons_standalone — `0.5.0 gameplay-stats`
- haute_capitale_fusils — `b3`
- Haute Capitale RPG — `TEST3-RC2`
- Hazennstuff — `SPELLCOMPAT1`
- NexusCharacters — `0.8.0-alpha1.1 LEGACY-SKIN-COMPAT`
- Spell Engine — `TEST3-RC7 HAZENN-FULLCOMPAT`
- Witcher Class — `FOOTWORK TEST3-RC1`

## JAR serveur identifiés mais pas encore archivés

- Spell Power — **RC6-CLASSFORMAT-FIX** ; le binaire exact déployé n'est pas encore disponible.
- capitale_creatures_bundle — **1.2.16**
- capitale_heraldry — **0.1.7**
- MMO Music Zones — **1.2.7 indivis-dungeon-biome-FULL**
- haute-capitale-dialogue — **b7**
- haute-capitale-quests — **b3**
- haute-capitale-pirates — **b4**
- haute-capitale-party — **b9**
- haute-capitale-metiers — **b12**
- haute-capitale-orcs — **b9**
- haute-capitale-spawns — **b6**
- hc-necromancer — **b3**
- capitale_entities — **0.3.0-alpha.1**

Les versions déployées de référence restent consignées dans `server-manifest.yml`.
