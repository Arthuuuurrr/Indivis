# Nexus Characters PRE18 — validation

PRE18 replaces the PRE17 hair-only render path with one generic, single-model
Skin Layers composition path. It promotes:

- hair on the head and body;
- classic facial-hair textures (modeled dwarf/cultural beards stay unchanged);
- outfit base pixels on torso, arms and legs, plus pre-existing outer pixels.

Assets and their selection ranges are detected from the active resources rather
than an index whitelist. New hair, outfit and classic facial-hair PNG files join
the UI cycle, marker serialization, composition and 3D pipeline without another
code change. Resource-pack additions are included and the catalog is rebuilt on
resource reload.

For identifiers that remain stable across later additions, continue the numeric
sequence with `hair_14.png`, `hair_15.png`, etc.; `facial_07.png`,
`facial_08.png`, etc.; and `outfit_39.png`, `outfit_40.png`, etc. Existing
`hair_long_NN.png` and `hair_short_NN.png` naming remains supported. The sparse
hair cycle preserves earlier saved IDs when a new short hair and a new long hair
are introduced in either order.

## Exact environment checked

- Minecraft 1.21.11 intermediary client classes
- Fabric Loader 0.19.5
- Fabric API 0.141.6+1.21.11
- 3d-Skin-Layers 1.10.2 (`736f076b9f45bab1c118166857f46729f782ddd481a0e4e4ee0bc110643f2756`)
- Sodium 0.8.7+mc1.21.11
- ImmediatelyFast 1.14.2+1.21.11
- Java 21

## Regression coverage

`RealGeometryTest` uses Minecraft's real `NativeImage` and matrix classes and
Skin Layers' real `MeshHelper`/voxel implementation. It checks all 13 active
hair assets, all 6 classic facial-hair assets, all 33 outfits, the 98 current
hair/facial-hair combinations, wide and slim limbs, synthetic future assets,
configuration restoration, compatibility mesh rebuilding, finite vertices,
stable sparse hair and beard IDs, transitions to modeled beards, mandatory dwarf
beards and resource-reload cache invalidation.

Final result:

```text
REAL_GEOMETRY_PASS hairs=13 classicBeards=6 outfits=33 checks=2923269 vertices=973992
```

The transformed Fabric client was also checked to ensure the PRE18 player-model
callback executes after Skin Layers' callback. A real off-screen client rendered
the worldless preview from the front, side and rear, then opened an integrated
world and exercised the world renderer. A test resource mod added long hair 14,
short hair 1006, classic beards 7 and 9 (no 8) and outfit 39; all five were discovered,
serialized, composed and rendered without altering Nexus code
(`DYNAMIC_CATALOG_PASS hair=15 facial=8 outfits=34`). A real resource reload also
passed (`RESOURCE_RELOAD_PASS`), with all added resources still selectable. This catches
mixin order, texture upload, resource discovery, render-queue and widget/model
integration errors that unit stubs cannot detect.

The actual transformed `CharacterCreationScreen` was opened and its hair and
outfit callbacks invoked. Its marker serialization preserved the sparse future
IDs (`CREATION_UI_PASS hair=1006 facial=9 outfit=39`), not merely direct calls to
the catalog helper. The final JAR was reproduced byte-for-byte in a separate
clean build.

## Deliberate compatibility behavior

- One player model and one composed skin texture are submitted; the removed
  secondary hair widget cannot create depth/framebuffer ordering artifacts.
- Native Skin Layers LOD, helmet suppression and per-body-part enable flags are
  retained in world rendering.
- Skin Layers `fastRender` is restored immediately after masked mesh creation.
- Iris compatibility and Sodium workaround settings participate in the mesh
  cache key, so toggling either rebuilds geometry instead of reusing stale data.
- F3+T/resource reload clears composed-skin and mesh plans both before and after
  the reload, preventing early access from freezing the old asset catalog.
- Existing modeled beards, ears, ornaments, persistence and server behavior are
  not replaced.

SHA-256 of the validated JAR:

```text
06ed600dabdd457667ae531f6dedf814618c1eb21f1eee9f2b98b0fbb2f54fb6
```
