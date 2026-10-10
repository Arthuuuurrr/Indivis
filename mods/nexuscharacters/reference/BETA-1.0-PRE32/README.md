# NexusCharacters PRE32 — independent 3D hand surfaces

Base: exact PRE31 (`d0d725337353dcc7328b86a46ace83ca82b66b7680416a8b555900de764fe203`).

`FirstPersonSurfaceSupport` preserves PRE31's local surface construction, but processes only the submitted arm. Each weakly held model has separate bounded caches for left and right arms. Entries retain exact template/shape identity, model cube identity, slim/classic choice and relative sleeve transform. Unrelated world clears detach injections and keep the hand cache; resource reloads discard it. World/inventory meshes remain in `PosedSurfaceSupport` and its unchanged clipping code. An unfamiliar arm uses the retained PRE31 path.

The patch changes only GenericSkinLayerSupport's hand invocation/resource cleanup and PosedSurfaceSupport's clear hook. All assets, geometry builders, UV/depth/clipping math, menus, networking and persistence are preserved.

## Reconstruct the exact tested JAR

Python 3, no game or compiler required:

```sh
python assemble.py /path/to/PRE31.jar /path/to/PRE32.jar
```

The input and output hashes, compiled patch contents and metadata are checked against `build-manifest.json`. The reconstruction is deterministic.

## Compile and run the game regressions

`build.py` and `run_client.py` use the sibling `pre31/` runtime from the previous reference build (Temurin 21, the remapped 1.21.11 client, Fabric and SkinLayers). `runtime-extra/` holds the official exact-version Iris, Accessories, owo and EasyNPC test dependencies. These external binaries are not added to this source package.

```sh
python build.py
python run_client.py
python run_client.py --baseline
python run_client.py --hand-only
python run_client.py --world-diag --compat
python run_client.py --world-diag --baseline --compat
python run_client.py --future-assets
```

`PRE32_DISPLAY` selects an Xvfb display when different isolated game directories run concurrently. The world harness includes F1, resource reload, swing, inventory, two-hand map rendering and additional model-pass/clear regressions. The latter are explicit test conditions, not a claim that the user's exact shader/armor invalidation was identified.

See `PRE32-verifications.md` and `qa/` for measurements, geometry checks and remaining Lion-Port limits. Profiling percentages are inclusive time of one Render thread, not FPS or additive whole-machine CPU. Tests use software rendering and a reduced modpack; the user's actual world, shader and armor setup is not copied here.
