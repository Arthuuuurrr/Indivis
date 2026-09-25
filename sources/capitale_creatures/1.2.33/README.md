# Capitale Creatures 1.2.33 — Cube Animals eagle nest lifecycle

Base binaire : `capitale_creatures_bundle-1.2.32-fabric-1.21.11-CORRUPTION-FR.jar`.

## Correctif

Le sous-mod `META-INF/jars/cubeanimals.jar` est modifié uniquement sur :
- `net/suprk/ufauna/block/custom/EagleNest.class`
- `net/suprk/ufauna/entity/custom/EagleEntity.class`

Comportement :
- tout nid naturel créé par un aigle reçoit un contrôle à **6000 ticks chargés** (5 minutes) ;
- si le nid est vide et `placed_by_player=false`, il est supprimé ;
- s'il contient un œuf, le cycle amont crack → hatch continue normalement ;
- après éclosion, un nouveau contrôle est programmé 6000 ticks plus tard ;
- un nid placé comme bloc par un joueur est désormais marqué `placed_by_player=true` ;
- retirer un œuf manuellement marque aussi le nid comme géré par joueur ;
- les nids gérés par joueur ne sont jamais supprimés par ce nettoyage.

Le délai de 6000 ticks est volontairement identique au premier tick d'incubation naturel : il n'introduit donc pas de tick concurrent susceptible de retarder l'éclosion.

## Invariants 1.2.32 conservés

Aucun changement sur :
- pools de spawn ;
- densité des orcs ;
- IA marine ;
- dégâts ;
- traductions FR ;
- autres JAR imbriqués.

## Artefacts

- bundle 1.2.33 SHA-256 : `cb430df199651d6a914f7ba2d6d447e7a501d49c057f06d12dbb630685f49b78`
- cubeanimals imbriqué patché SHA-256 : `69d9e3b966c766b9673e07a407588c30b10790ccb109b90ce30684af49268037`

## Validation statique

Comparaison ZIP 1.2.32 → 1.2.33 :
- 126 entrées inchangées octet pour octet ;
- changements limités à `fabric.mod.json` et `META-INF/jars/cubeanimals.jar` ;
- ajout d'une note de cycle de vie dans `META-INF/`.

Test en jeu encore requis avant fusion.
