# NexusCharacters PRE31 — first-person surface reuse

Base: exact PRE30 SHA-256 `1c164be6dc8eef9372120ae384b65c5d2be8ebaa7fed67461b986726a5cff922`.
Output: `d0d725337353dcc7328b86a46ace83ca82b66b7680416a8b555900de764fe203`.

The patch changes two existing classes and adds one helper. SurfaceGeometry.write delegates to a primitive float serializer, preserving the native layout. A six-instruction guard skips first-person serialization only when the previous mode was first person and local templates/relative transforms are unchanged. Pose snapshots and native mesh reinstallation are still performed. All clipping instructions are preserved.

Reproduce the exact tested JAR using `python assemble.py /path/to/PRE30.jar /path/to/PRE31.jar`. This uses the preserved compiled patches, verifies all hashes and needs no recompilation. `build.py` recompiles from source with Java 21, the exact base, Minecraft intermediary and 3D Skin Layers. Runtime setup scripts are under tools/. Test harnesses never ship in the production JAR.

Tests: 4,000 serialization cases / 2,962,626 raw-float assertions; 320 canonical geometry states matching PRE30; 2,337 candidate render assertions; 23 menu captures pixel-identical, 688 assertions; 52 world assertions over 346 frames with actual reload and first/third-person switching; three future PNG ids recognized and rendered, 19 assertions. Bytecode audit confirms the original pose body after removing the six-instruction guard, and the original clipping math.

Measured median CPU/bytes per invocation on the real client render thread, diagnostics off:

| Path | PRE30 CPU | PRE31 CPU | PRE30 bytes | PRE31 bytes |
|---|---:|---:|---:|---:|
| First-person lower-pose animation, local surfaces stable | 368.805 us | 2.515 us | 2143280 | 0 |
| Third-person animated pose | 71.985 ms | 71.906 ms | 105348118 | 101370812 |
| Static posed, 100000 warmup calls | 1.041 us | 1.065 us | 0 | 0 |
| Full preview, 100000 warmup calls | 25.615 us | 27.041 us | 18992 | 18992 |

Only first-person CPU improvement and allocation reductions are established. No third-person CPU improvement, global FPS or spectator-cause diagnosis is claimed. The full French report and raw acceptance/metrics files are retained here. Runtime: Minecraft1.21.11, Temurin21.0.12.1, FabricLoader0.19.5, API0.141.6, SkinLayers1.10.2, Sodium0.8.7, ImmediatelyFast1.14.2, llvmpipe; PuffishSkills0.19.0 in the world test.
