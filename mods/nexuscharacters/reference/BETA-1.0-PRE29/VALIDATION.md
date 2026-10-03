# PRE29 — clean labels and initial orientation

Minecraft 1.21.11, Java 21, Fabric Loader 0.19.5, Fabric API 0.141.6, 3D Skin Layers 1.10.2, Sodium 0.8.7 and ImmediatelyFast 1.14.2.

Candidate SHA256: `b09135fd42719bcf0e4753db22615db3bce4c8ff31783ff01a5ad8fdf202e96c`.

- Removed all rectangles behind individual styled text runs. Names and buttons now keep a uniform background. Input fields share one parchment background regardless of text color.
- Light-colored text keeps its original color with a symmetric 0.35 GUI-pixel dark edge, without a directional shadow. Dark text remains plain. Applied to labels and native input text without replacing editing, focus, selection or cursor logic.
- Synchronize a newly created worldless preview widget to the saved yaw immediately after positioning and before submitting body or cosmetics. Previously its body could start at vanilla 30 degrees while separate cosmetics used 30 degrees plus the saved rotation.
- Assets, palette ranges, banner selection, slider logic, meshes and server code remain byte-identical to PRE28. Only the helper, field-contrast mixin, worldless viewport mixin and version metadata change.

Final live Minecraft test: **23 captures, 688 assertions passed**, 1920x1080, five races, GUI scales 2/3/4, creation and selection, actual slider clicks and rotations. Names include black/white, gold, green and blue with accented text. Screenshots visually inspected for the removal of highlighting and the legibility of light-colored names.

The test asserts rendered widget yaw equals cosmetic yaw before any drag, including switching to a fresh character with a nonzero saved yaw. Same test on PRE28 fails with `Initial body/cosmetic yaw mismatch before any drag` — the negative control confirms the test reproduces the previous defect. PRE29 passes.

The isolated offline renderer produces expected Mojang/Realms connection warnings. This validates the identified initialization mismatch; it does not reproduce the user's exact saved character files, which were not provided.
