# Capitale Creatures 1.2.28 / BETA 0.22

## Aquatic AI
- Fixed `CapitaleCreaturesMarineLocomotion1210.combatTarget`: active combat-target pursuit is no longer restricted to `myths_of_the_sea:*`.
- All entities already in the STRICT marine set can steer in 3D toward their active target while that target is in water.
- Existing water-column steering, surface/bottom guards, dry-out behavior and species swim-speed curve are retained.

## Aquatic population
- runtime interval: 80 -> 60 ticks
- managed nearby cap/player: 12 -> 16
- candidate attempts: 24 -> 30
- shark: weight 5 -> 2; group 1-2 -> 1
- corpsefish: weight 10 -> 12; group 2-5
- gluttonfish: weight 1 -> 2
- piranha: weight 10 -> 9; group 4-7
- aquatic dino runtime rules now carry explicit per-variant weights (Aegirocassis 2, Dunkleosteus 3, Mosasaurus 2)

## Terrestrial pool
- maggot rollback: weight 7 -> 5
- rare corruption orc: `autonomous_orc_mobs:female_orc_warrior_red`, weight 1, singleton, corruption-only
- dungeon orc balance from 1.2.27 is unchanged
- BETA 0.21 dino tank damage curve, HP and armor retained

## Classic fauna audit
All entities from CubeAnimals, HMobs, DeerMod, WanderingWildlife, AnimalGarden Hippopotamus, White Rhinoceros and Western Gorilla present in the bundle inventory have active profiles. Remaining unprofiled entries are primarily specialized Autonomous Orc variants and Chaos MMO boss/event mobs and are intentionally not auto-added.

## Artifacts
- `capitale_creatures_biomes_BETA_0_22.zip`
- `capitale_creatures_bundle-1.2.28-fabric-1.21.11-AQUATIC-AI-POOL.jar`

SHA-256:
- datapack: `c824cb1ff3104b5350e9466670232c807f974836e9236884a41ec0de489f62db`
- bundle: `66c812475273c687f011db7813fbee544929e2ef141f88db26f8d0780f4be504`
