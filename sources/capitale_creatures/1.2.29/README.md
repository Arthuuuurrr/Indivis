# Capitale Creatures 1.2.29 / BETA 0.23

## Marine combat
- Direct 3D pursuit toward nearby players who are actually touching water.
- Native navigation is stopped while the direct chase controller owns movement.
- Native melee target is only enabled inside a species-specific engage range, reducing excessive attack reach.
- Corpsefish swimmer acquisition: 28 blocks.
- Gluttonfish: 30 blocks.
- Piranha: 18 blocks.
- Mosasaurus: 28 blocks.
- Dunkleosteus: 24 blocks.
- Aegirocassis: 18 blocks.
- Existing BETA 0.22 aquatic population weights are retained.

## Mountain Maggot correction
- Maggot remains weight 5 globally.
- Empire high-mountain tag excludes windswept_gravelly_hills and snowy_slopes from the injected Maggot rule.
- Runtime fallback discards chaos_mmo_ai:maggot at Y >= 115 without loot, preventing native/mod-origin spillover on mountain summits.

## Artifacts
- capitale_creatures_biomes_BETA_0_23.zip
- capitale_creatures_bundle-1.2.29-fabric-1.21.11-MARINE-COMBAT-DIRECT.jar

SHA-256:
- datapack: fb4133a0554b1775e71f81daf962bfa3551d6639b0d3266d98e45c36b7c12f0c
- bundle: 041e527ef08ae4caef6e31b2f4f15bbbdca0284e5941fc4dc0460a0af1cd0608
