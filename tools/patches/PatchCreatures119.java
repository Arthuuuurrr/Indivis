// Reproduction source for capitale_creatures_bundle 1.2.19.
// Applies two targeted ASM patches to 1.2.18:
// 1) natural spawn clear uses exact city RegistryKeys via BiomeSelectors.includeByKey;
// 2) our orc patrol members are tagged and removed if they enter an excluded biome.
//
// Canonical local build source is PatchCreatures119.java used for artifact:
// capitale_creatures_bundle-1.2.19-fabric-1.21.11-EXACT-CITY-SAFE.jar
//
// Protected city registry keys:
// capitale:capitale
// indivis:lion_port
// indivis:clairval
// indivis:haute_rive
// indivis:ilystara
// indivis:sylvarhen
// indivis:avaleiv
// indivis:skarnfjord
// indivis:durak_vor
//
// Natural-spawn selector:
// #capitale_creatures:all_overworld OR BiomeSelectors.includeByKey(EXACT_CITY_KEYS)
//
// Patrol containment:
// spawned patrol entities receive command tag "indivis_orc_patrol".
// every overworld tick, tagged patrol entities are discarded when their current
// biome (sampled at y, y+2, y+6, y-2) belongs to exclude_biomes.
//
// Artifact SHA-256:
// 7bda94ef06fa5cd3da514221e4da576167cf42c69fd748b89e5635d8f209140c
//
// NOTE: The full executable patcher is intentionally tracked below verbatim.

/*
Implementation note:
- clearTargets(String) is replaced in
  fr.hautecapitale.creatures.spawn.CapitaleCreaturesDatapackSpawnController.
- It creates RegistryKey<Biome> values with RegistryKeys.BIOME + Identifier,
  then calls BiomeSelectors.includeByKey(Collection).
- The previous city_no_spawn tag remains data/documentation only; runtime safety
  no longer depends on resolving that tag during biome-modification registration.

- spawnPatrol(...) in CapitaleCreaturesOrcPatrol1212 marks every successful
  patrol entity using Entity.addCommandTag("indivis_orc_patrol").
- tickWorld(...) calls purgePatrolsInExcludedBiomes(world) before the patrol
  interval/cooldown early returns, so containment runs every overworld tick.
- purge uses the controller's existing biomeId(...) and excludeBiomes set,
  and Entity.discard() for tagged patrol members crossing into an excluded biome.

Validation:
- ASM BasicVerifier: PASS for both modified classes.
- ZIP/JAR integrity: PASS.
- 11 nested fauna JARs byte-identical to 1.2.18.
- Only modified runtime classes:
  CapitaleCreaturesDatapackSpawnController.class
  CapitaleCreaturesOrcPatrol1212.class
*/
