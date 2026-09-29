# Capitale Creatures 1.2.47 — Mixin metadata hotfix

Base runtime: `capitale_creatures_bundle 1.2.46`.
Companion datapack: **BETA 0.30 unchanged**.

## Startup crash fixed
1.2.46 compiled `SnakeJockeyGuardMixin` against a synthetic `@Mixin` annotation declared with `RetentionPolicy.RUNTIME`.

SpongePowered Mixin declares `@Mixin` with `RetentionPolicy.CLASS`. The runtime Mixin parser therefore looked for an invisible CLASS annotation and rejected the 1.2.46 class as if it had no `@Mixin` annotation.

1.2.47 rebuilds the exact same event-driven jockey guard with annotation retention matching SpongePowered:
- `@Mixin` = CLASS;
- `@Inject` = RUNTIME;
- `@At` = RUNTIME.

It also uses the canonical public target form:
`@Mixin(value = class_1297.class, remap = false)`.

## Jockey logic unchanged
The hook still intercepts:
`Entity.startRiding(Entity, boolean, boolean)`
intermediary selector:
`method_5873(Lnet/minecraft/class_1297;ZZ)Z`.

Only `minecraft:skeleton` trying to ride:
- `cubeanimals:rattlesnake`;
- `capitale_entities:snake`

is discarded and rejected.

There is no per-tick scan.

## Invariants
- BETA 0.30 unchanged.
- No spawn-balance changes.
- No Orc changes.
- 11 nested creature JARs remain byte-for-byte identical.
