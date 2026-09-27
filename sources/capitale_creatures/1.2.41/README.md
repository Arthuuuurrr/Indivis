# Capitale Creatures 1.2.41 — Gluttonfish abyssal depth authority

Base : `capitale_creatures_bundle 1.2.40`.
Datapack : **BETA 0.28 inchangé**.

## Régression observée
Le `chaos_mmo_ai:gluttonfish` pouvait remonter et rester à la surface malgré sa préférence abyssale introduite dans la couche marine 1.2.38/1.2.39.

Le réglage de profondeur existait toujours, mais restait trop souple :
- la vitesse verticale était seulement mélangée avec la cible profonde ;
- l'IA native Chaos MMO pouvait réinjecter une remontée ;
- toute cible conservée par l'IA native court-circuitait la nage idle abyssale.

## Correction
Nouvelle couche dédiée `CapitaleCreaturesGluttonDepth141`, enregistrée après `MarineIdle139` :
- contrôle au START_WORLD_TICK et END_WORLD_TICK ;
- hors combat sous-marin proche, une cible terrestre/surface ou trop lointaine est relâchée ;
- si le Gluttonfish entre dans les ~40 % supérieurs d'une colonne d'eau profonde, une vitesse descendante minimale est imposée ;
- bande de croisière visée : environ les 20-30 % inférieurs de la colonne d'eau ;
- un combat contre une cible réellement immergée à <=24 blocs reste libre de remonter ;
- respiration et orientation 1.2.39 conservées.

## Validation
- classe compilée Java 21 ;
- ASM CheckClassAdapter : PASS ;
- JAR valide ;
- diff 1.2.40 -> 1.2.41 : `fabric.mod.json` modifié, ajout de `CapitaleCreaturesGluttonDepth141.class` et d'une note META uniquement ;
- aucun JAR imbriqué modifié ;
- BETA 0.28 inchangé.

SHA-256 bundle :
`09d5639a5a18c614dba394845bb90a58353aa4fce82ef29048554cb63ffeea13`
