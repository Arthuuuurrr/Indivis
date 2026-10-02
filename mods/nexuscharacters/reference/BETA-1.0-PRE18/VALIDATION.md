# Nexus Characters PRE18 — validation

PRE18 replaces the PRE17 hair-only render path with one generic, single-model
Skin Layers composition path. It promotes:

- hair on the head and body;
- classic facial-hair textures (modeled dwarf/cultural beards stay unchanged);
- outfit base pixels on torso, arms and legs, plus pre-existing outer pixels.

Assets are detected by their resource path rather than an index whitelist.
Consequently, a future asset referenced by the appearance catalog uses the same
3D pipeline without adding another renderer or another hard-coded range.

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
configuration restoration, compatibility mesh rebuilding, finite vertices and
resource-reload cache invalidation.

Final result:

```text
REAL_GEOMETRY_PASS hairs=13 classicBeards=6 outfits=33 checks=2923240 vertices=973992
```

The transformed Fabric client was also checked to ensure the PRE18 player-model
callback executes after Skin Layers' callback. A real off-screen client rendered
the worldless preview from the front, side and rear. This catches mixin order,
texture upload, render-queue and widget/model integration errors that unit stubs
cannot detect.

## Deliberate compatibility behavior

- One player model and one composed skin texture are submitted; the removed
  secondary hair widget cannot create depth/framebuffer ordering artifacts.
- Native Skin Layers LOD, helmet suppression and per-body-part enable flags are
  retained in world rendering.
- Skin Layers `fastRender` is restored immediately after masked mesh creation.
- Iris compatibility and Sodium workaround settings participate in the mesh
  cache key, so toggling either rebuilds geometry instead of reusing stale data.
- F3+T/resource reload clears composed-skin and mesh plans.
- Existing modeled beards, ears, ornaments, persistence and server behavior are
  not replaced.

SHA-256 of the validated JAR:

```text
7751608b3f9d92e087802fe570b154251651f6150fecd49d71833ba6e375d270
```
