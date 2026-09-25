# Capitale Creatures Bundle 1.2.19 — Exact City Safe

## Why 1.2.18 was insufficient

The historic `capitale:capitale` biome has empty spawn lists and is not normally
targeted by mod biome-spawn injections. The new `indivis:*` cities also declare
empty spawner lists, but relying on the custom `#capitale_creatures:city_no_spawn`
tag in the Fabric biome modification selector did not reliably reproduce the same
runtime result.

The 1.2.19 fix therefore removes that dependency from the runtime selector.

## Natural spawn protection

In `CapitaleCreaturesDatapackSpawnController`, the POST_PROCESSING
`clearSpawns()` selector is now:

- existing `#capitale_creatures:all_overworld`
- OR an exact `BiomeSelectors.includeByKey(...)` collection for:
  - `capitale:capitale`
  - `indivis:lion_port`
  - `indivis:clairval`
  - `indivis:haute_rive`
  - `indivis:ilystara`
  - `indivis:sylvarhen`
  - `indivis:avaleiv`
  - `indivis:skarnfjord`
  - `indivis:durak_vor`

This targets the registry entries themselves, rather than relying on a datapack
biome tag being resolved when Fabric registers biome modifications.

## Orc patrol containment

The patrol system is the custom Haute Capitale event modeled after vanilla
pillager patrols.

1. Existing candidate-position exclusions remain.
2. Every successfully spawned patrol member gets command tag
   `indivis_orc_patrol`.
3. On every Overworld tick, before interval/cooldown early returns, tagged patrol
   members are checked against `exclude_biomes`.
4. The check samples current Y as well as Y+2, Y+6 and Y-2 to reduce 3D-biome
   boundary issues.
5. A tagged patrol member entering an excluded biome is discarded without loot.

This means a patrol cannot remain in a city even if it spawned just outside the
city boundary and subsequently walked in.

## Compatibility

The 11 bundled fauna JARs are byte-identical to 1.2.18.

Only these runtime files changed:
- `CapitaleCreaturesDatapackSpawnController.class`
- `CapitaleCreaturesOrcPatrol1212.class`
- `fabric.mod.json`
- `META-INF/INDIVIS_CITY_SAFE_1_2_19.txt`

Artifact:
`capitale_creatures_bundle-1.2.19-fabric-1.21.11-EXACT-CITY-SAFE.jar`

SHA-256:
`7bda94ef06fa5cd3da514221e4da576167cf42c69fd748b89e5635d8f209140c`

Validation:
- archive integrity: PASS
- ASM BasicVerifier: PASS on both modified classes
- nested fauna JAR comparison: 11/11 unchanged
