# Capitale Creatures 1.2.44 — balance only, no Orc changes

Base runtime: `capitale_creatures_bundle 1.2.41`.
Companion datapack: `capitale_creatures_biomes_BETA_0_30`, itself rebuilt from BETA 0.28.

## Included changes
- `chaos_mmo_ai:maggot`: weight 5 -> 7; group 1-2 -> 1-3.
- `capitale_entities:snake`: weight 6 -> 4; group remains 1-2.
- `cubeanimals:rattlesnake`: weight 10 -> 7; group remains 1-2.
- Mystic Triceratops:
  - desert/badlands: unchanged;
  - plains/sunflower plains: `frequency_percent=70` (~33% lower effective family frequency).
- French localisation overlay:
  - `entity.hmobs.brown_bear = Ours brun`.

## Explicitly NOT changed
This build intentionally discards the Orc patrol experiments from 1.2.42/1.2.43.

Compared with 1.2.41 / BETA 0.28, these are byte-for-byte unchanged:
- `fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212.class`
- `data/capitale_creatures/spawn_rules/orc_patrol_rules.json`
- `data/capitale_creatures/tags/worldgen/biome/city_no_spawn.json`
- every bundled nested JAR, including Autonomous Orc Mobs.

The only Java logic change is generic support for the optional per-rule `frequency_percent` multiplier in `CapitaleCreaturesDatapackSpawnController`.

## Static validation
- OrcPatrol class SHA matches 1.2.41 exactly.
- Orc patrol rules and city_no_spawn SHA match BETA 0.28 exactly.
- all 11 nested JARs match 1.2.41 exactly.
- ASM BasicVerifier PASS:
  - DatapackSpawnController: 22 methods
  - WeightScale142: 2 methods
- bundle JSON/metadata: 62 parsed successfully.
- datapack JSON/metadata: 54 parsed successfully.
- JAR/ZIP integrity checks PASS.

Bundle SHA-256:
`91c70bb653ca2e6806de1b7a56afeb2aad8ae16dc5ba77a0b207a897c7134b6d`

Datapack SHA-256:
`514e72592cc9012a94c3c24fad4052522efcb072ac3e6861e6bb8de769dc4bb2`
