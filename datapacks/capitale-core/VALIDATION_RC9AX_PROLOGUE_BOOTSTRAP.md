# Capitale Core 1.5.6 RC9AX — Prologue bootstrap fix

Base stricte : RC9AW.

## Cause

Le code `capitale:player/bootstrap_runtime_v100_self` indique qu'il doit être déclenché par
l'advancement one-shot `capitale:player/bootstrap_runtime_v100`, mais ce fichier d'advancement
était absent du datapack. Le tick permanent avait précisément retiré cette initialisation.

Conséquence : un nouveau personnage Nexus avec vault joueur/advancements vides rejoignait le
worldspawn sans exécuter `player/init` ni `spawn/start_prologue_self`.

## Correction

Ajout de :

`data/capitale/advancement/player/bootstrap_runtime_v100.json`

avec un critère `minecraft:tick` et la reward :

`capitale:player/bootstrap_runtime_v100_self`

Pour un nouveau personnage (pas de tag `capitale_init`) le chemin devient :
bootstrap -> first_join_to_prologue_start_self -> player/init -> start_prologue_self,
donc TP 764 299 1101 + spawnpoint personnel et démarrage de QUEST_SPAWN.

Pour un personnage déjà initialisé, le bootstrap ne redémarre pas le prologue : il exécute
seulement les migrations/ensure_runtime prévues par le code existant.

Aucune logique de quête, de créatures, d'Orcs, de biomes, de villes, de feu ou de PNJ n'a été modifiée.
