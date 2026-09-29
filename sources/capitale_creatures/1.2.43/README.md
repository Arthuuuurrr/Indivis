# Capitale Creatures 1.2.43 — event-safe city patrols

Base: `capitale_creatures_bundle 1.2.42`.
Datapack: **BETA 0.29 unchanged**.

## Design correction

1.2.42 prevented ambient patrols from spawning in/near city biomes, but also purged tagged patrol entities after they entered the city buffer. That would conflict with future scripted city-assault events.

1.2.43 keeps only the spawn-time protection:
- the 16-block city buffer remains active for ambient patrol spawn candidates;
- the anchor and each patrol member still require a valid non-city spawn position;
- `purgeNearCities` is intentionally a no-op for binary compatibility;
- existing patrols/orcs may move into cities normally;
- future scripted event orcs are not deleted by the creature bundle.

This cleanly separates **ambient spawn prevention** from **event/gameplay movement**.

No datapack change. BETA 0.29 remains the required companion for the 1.2.42 balance changes.

## Static validation
- `purgeNearCities(Object)` bytecode is exactly a return;
- 1.2.42 -> 1.2.43 changed only `fabric.mod.json` and `CapitaleCreaturesPatrolCityGuard142.class`, plus one marker file;
- no nested JAR changed;
- archive validation PASS.

Bundle SHA-256:
`e1b64e8ba2123e7c1be0c492b01648674868018b77f803b649b5a76065406b59`
