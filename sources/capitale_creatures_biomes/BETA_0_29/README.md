# Capitale Creatures Biomes BETA 0.29

Base: BETA 0.28.

## Small-hostile balance
- chaos_mmo_ai:maggot: weight 7, group 1-3.
- capitale_entities:snake: weight 4, group 1-2.
- cubeanimals:rattlesnake: weight 7, group 1-2.

## Mystic Triceratops
The combined dry/plains rule is split:
- desert/badlands: unchanged family weight 1;
- plains/sunflower plains: family weight 1 with frequency_percent 70.

Bundle 1.2.42 adds support for this optional rule multiplier.

## Orc patrols
- exact city exclusions retained;
- optional future city aliases added to city_no_spawn / patrol exclusions;
- bundle 1.2.42 enforces a 16-block city-border buffer and periodic purge.

## Compatibility
- deep-ocean BETA 0.27 tuning unchanged;
- BETA 0.28 optional custom-biome references retained;
- normal oceans/rivers unchanged.

SHA-256: `e45d43a0cde90aeef30634197211ad70eeb64e3a2581c86057d4b07cbb8f3311`.
