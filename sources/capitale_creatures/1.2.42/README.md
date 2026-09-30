# Capitale Creatures 1.2.42 — balance + city patrol hardening

Base: `capitale_creatures_bundle 1.2.41`.
Datapack: `capitale_creatures_biomes_BETA_0_29`.

## Small-hostile balance
- `chaos_mmo_ai:maggot`: weight 5 -> 7, group 1-2 -> 1-3.
- `capitale_entities:snake`: weight 6 -> 4, group remains 1-2.
- `cubeanimals:rattlesnake`: weight 10 -> 7, group remains 1-2.

## Mystic Triceratops
The old dry+plains rule is split:
- dry biomes: unchanged family weight;
- plains/sunflower plains: `frequency_percent=70`.

The datapack spawn controller is patched with a generic optional `frequency_percent` rule key. For the four Mystic Triceratops variants, the effective total internal weight drops from 12 to 8 in plains (~33% reduction), while dry-biome frequency is unchanged.

## Orc patrol city safety
The existing exact biome blacklist is retained and reinforced:
- patrol anchors are rejected when a city biome is detected within a sampled 16-block border buffer;
- every individual patrol member goes through the same `surfaceAt` validation;
- tagged patrols (`indivis_orc_patrol`) entering the same city buffer are periodically purged;
- known current city IDs plus optional future aliases are included.

This is designed to cover biome-border cases where a patrol could be anchored immediately outside a city and visually appear inside it.

## Localisation
Outer resource overlay:
`entity.hmobs.brown_bear = Ours brun`.

## Compatibility retained
- 1.2.41 Gluttonfish abyssal depth authority;
- 1.2.39 marine breathing/orientation;
- 1.2.40 optional custom-biome refs for solo worlds;
- 1.2.37 CubeAnimals egg pickup/lifecycle;
- nested bundled JARs unchanged.

## Static validation
- ASM BasicVerifier PASS:
  - DatapackSpawnController: 22 methods
  - OrcPatrol1212: 28 methods
  - PatrolCityGuard142: 10 methods
  - WeightScale142: 2 methods
- bundle JSON: 62 valid;
- datapack JSON/metadata: 54 valid;
- bundle and datapack archives valid;
- 19 custom city refs remain `required:false`;
- nested JAR hashes unchanged.

Bundle SHA-256:
`a4bf013cb67700b4f346a6d69f37dc65b4f13c3db1a5c024a8dce1cf901b9aaa`

Datapack SHA-256:
`e45d43a0cde90aeef30634197211ad70eeb64e3a2581c86057d4b07cbb8f3311`

Runtime validation required before merge/deployment manifest update.
