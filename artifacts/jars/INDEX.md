# Bibliothèque JAR — serveur courant

Ce dossier contient uniquement les **JAR correspondant à l'état serveur de référence**. Il ne sert plus d'archive de toutes les RC historiques.

Les binaires sont stockés avec **Git LFS**. Git conserve un pointeur contenant le SHA-256 et la taille ; le JAR complet est récupéré lors d'un checkout LFS.

## Règle

- **1 module = 1 JAR actif maximum**.
- La version conservée doit correspondre à la version réellement installée sur le serveur de référence.
- Une RC plus récente mais uniquement présente sur le client ou dans nos fichiers de travail n'est pas mise ici.
- Les copies Windows `(1)`, `(2)`, etc. ne sont jamais considérées comme de nouvelles versions.
- Si le JAR serveur exact n'est pas disponible dans nos fichiers, on préfère laisser le module absent plutôt que d'archiver une mauvaise version.

## JAR serveur actuellement archivés

- Arsenal — `PRIMARY-ORDER1`
- AzureLibArmor — `TEST3-RC1`
- capitale_admin_commands — `1.0.2 SERVER_ONLY_EVENT_ACCESS_FIX`
- capitale_armor_test — `0.7.3 local-player-first-person-only`
- capitale_housing — `BETA 0.1.0 HOTFIX2`
- capitale_rp_hud — `1.3.2 NEXUS_AUTHORITY_FINAL_ARMOR40`
- capitale_skills_items — `1.7.18 EVENT_ACCESS_FIX`
- haute_capitale_fusils — `b3`
- Haute Capitale RPG — `TEST3-RC2`
- Hazennstuff — `SPELLCOMPAT1`
- NexusCharacters — `0.8.0-alpha1.1 LEGACY-SKIN-COMPAT`
- Spell Engine — `TEST3-RC7 HAZENN-FULLCOMPAT`
- Witcher Class — `FOOTWORK TEST3-RC1`

## Versions serveur identifiées mais JAR exact pas encore archivé

- Spell Power — **serveur : RC6-CLASSFORMAT-FIX**
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
- capitale_currency — **hotfix5**
- capitale_weapons_standalone — **0.5.0**
- capitale_entities — **0.3.0-alpha.1**

Ces modules seront ajoutés uniquement quand le **binaire correspondant exactement à la version serveur** sera disponible.

## Contrôle automatique

Le workflow `Check JAR library` exécute `tools/check_jar_duplicates.py`. Il échoue si :
- un module contient plusieurs JAR ;
- deux noms différents contiennent exactement le même binaire ;
- un prétendu JAR a une taille manifestement invalide.
