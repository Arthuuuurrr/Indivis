# NexusCharacters PRE27 — validation

Candidate SHA-256: `cb6169824f8f23b6afe3faa045b57ded89bb34eee12210e86c9401ad7568c15c`

Runtime validation completed on 2026-10-03 with Minecraft 1.21.11, Fabric Loader 0.19.5, Fabric API 0.141.6, SkinLayers3D 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2 and Pufferfish's Skills 0.19.0.

Result: `INDIVIS_WORLD_MENU_PASS captures=25 checks=6540`.

Coverage:
- selection and creation are separate screens;
- real integrated world loaded;
- five playable races;
- GUI scales 2, 3 and 4;
- top, middle and bottom scrolling layouts;
- all skin, iris, hair, facial-hair and marking palette values;
- mouse rotation, rear view and release;
- Mojang, external-player and custom skin modes;
- first/last name colors and styles retained after reinitialization;
- character creation persisted and read back;
- inherited PRE26 entries unchanged except the two intended JSON descriptors;
- world preview framing corrected and duplicate white name removed.

The headless runtime cannot validate live Mojang authentication. The existing network implementation was not modified.
