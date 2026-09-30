# Capitale Creatures 1.2.45 — suppression des skeleton jockeys sur serpents

Base runtime : `capitale_creatures_bundle 1.2.44`.
Companion datapack : **BETA 0.30 inchangé**.

## Problème traité
Les listes de spawn vanilla de l'Overworld sont déjà vidées par le contrôleur Haute Capitale, mais un mod peut créer un `minecraft:skeleton` comme passager lors de l'initialisation d'un serpent. Ce spawn secondaire ne dépend pas de la liste de spawn biome.

## Correctif
Un garde runtime très ciblé est exécuté à chaque tick :
- `cubeanimals:rattlesnake` : tout passager direct de type `minecraft:skeleton` est détaché puis téléporté sous le monde ;
- `capitale_entities:snake` : même traitement.

Le garde utilise `execute on passengers`, donc il ne vise pas les squelettes simplement proches d'un serpent.

## Invariants
- aucune modification des règles, classes, tags ou paramètres d'orcs ;
- aucun changement de poids/groupe/biome par rapport à 1.2.44 + BETA 0.30 ;
- les 11 JAR imbriqués restent byte-for-byte identiques à 1.2.44 ;
- les trois ressources orcs surveillées restent byte-for-byte identiques :
  - `fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212.class`
  - `data/capitale_creatures/spawn_rules/orc_patrol_rules.json`
  - `data/capitale_creatures/tags/worldgen/biome/city_no_spawn.json`

Le build GitHub vérifie ces invariants avant de publier le JAR versionné 1.2.45.
