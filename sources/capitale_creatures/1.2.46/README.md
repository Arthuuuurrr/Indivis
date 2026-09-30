# Capitale Creatures 1.2.46 — spawn-time skeleton jockey prevention

Base runtime: `capitale_creatures_bundle 1.2.45`.
Companion datapack: **BETA 0.30 unchanged**.

## Why 1.2.46
1.2.45 removed skeleton passengers with a datapack tick function. That fixed the symptom but scanned both snake entity types continuously.

Inspection of the embedded CubeAnimals 1.3 / Minecraft 1.21.11 bytecode shows that `RattleSnakeEntity.method_5943(...)` only selects the skin variant and delegates to its superclass; it does **not** create the skeleton rider itself. The rider is therefore introduced outside the rattlesnake's own spawn initializer.

## 1.2.46 implementation
A targeted Mixin intercepts Minecraft `Entity.startRiding(Entity, boolean, boolean)` at HEAD.

It acts only when:
- rider = `minecraft:skeleton`; and
- vehicle = `cubeanimals:rattlesnake` or `capitale_entities:snake`.

When both conditions match:
- the skeleton is discarded without loot;
- the riding call returns `false`;
- no passenger relationship is created.

There is **no per-tick entity scan**.

This interception is used instead of only overriding `canAddPassenger`, because forced riding can bypass normal rideability checks.

## Removed from 1.2.45
- `capitale_creatures:runtime/remove_snake_skeleton_jockeys`
- `capitale_creatures:runtime/remove_snake_skeleton_jockey`
- their tick-function registration.

## Invariants
- BETA 0.30 unchanged.
- No Orc class/rule/tag modification.
- All 11 nested creature JARs remain byte-for-byte identical to 1.2.45.
- Spawn weights/groups/biomes remain identical to 1.2.45.
