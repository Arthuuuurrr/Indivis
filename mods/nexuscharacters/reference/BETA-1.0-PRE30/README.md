# NexusCharacters PRE30 render performance

Base: exact PRE29 SHA256 b09135fd42719bcf0e4753db22615db3bce4c8ff31783ff01a5ad8fdf202e96c.
Candidate: 1c164be6dc8eef9372120ae384b65c5d2be8ebaa7fed67461b986726a5cff922.

Caches successful reflective member resolutions with ClassValue, retaining the original GenericSkinLayerSupport resolver and public-only SurfaceGeometry semantics. Reuses per-state pose scratch buffers with double buffering, model locking and separate storage for reentrant calls. The PRE29 geometry instructions, assets, UI, server and protocol are preserved. Optional diagnostic counters default to off.

Reproduce the exact tested JAR: `python assemble.py /path/to/PRE29.jar /path/to/PRE30.jar`. This requires only Python and the exact base; no recompilation. The manifest validates every patch class and final archive SHA256. `build.py` recompiles the helpers and applies the narrowly defined bytecode patch, using Java 21 plus the runtime jars.

Verification: 50,031 reflection assertions; classloader identity and GC; 3,001 scratch assertions across 300 real Minecraft transforms; 36 geometric subtraction regressions; 192 PRE29/PRE30 native polygon snapshots with identical float bits, UVs and normals; 23 menus with 688 assertions and 23 pixel-identical captures (one capture recaptured with 38 further assertions); 35 assertions/302 world frames including actual resource reload; three new PNG assets discovered and rendered in classic/slim models, 19 assertions.

Median CPU/bytes per invocation, actual Minecraft render thread, diagnostics off, five samples after warmup:

| Path | PRE29 CPU | PRE30 CPU | PRE29 bytes | PRE30 bytes |
|---|---:|---:|---:|---:|
| Static posed pass | 20.11 us | 3.52 us | 16864 | 0 |
| Full two-model preview | 68.98 us | 34.77 us | 56944 | 18992 |
| Animated posed pass | 72.83 ms | 72.28 ms | 105961676 | 105929060 |

The animated difference is too small to establish a CPU improvement. Its existing geometry rebuild dominates. These are per-call measurements with Mesa software rendering, not FPS claims or a diagnosis of all combat/spell/multiplayer stutters. No multiplayer load test or exhaustive third-party armor test was performed.

Runtime recovery: `prepare_runtime.py` requires runtime/version.json fetched from Mojang's 1.21.11 manifest. It downloads libraries and remaps the client. `download_assets.py` retrieves the required UI/font resources. `run_client.py` expects Fabric/3D SkinLayers/Sodium/ImmediatelyFast jars and an Xvfb test display. The runtime paths are relative to this reference directory; the archived evidence states the tested results, not a turnkey Gradle project. Tests use isolated game directories and fictional profiles.

Raw metrics and acceptance results are in qa/. The test-only future asset files are copied from existing PNGs to id 930 inside the harness jar. They are not shipped in PRE30.
