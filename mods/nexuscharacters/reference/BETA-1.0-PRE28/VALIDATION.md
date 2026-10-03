# PRE28 — focused menu corrections

Candidate SHA-256: `32247a901654df15cdfad011c85e3cf16ccb02848f893c342029581a81c43574`.

## Implemented

- Capital banner follows the creation race or selected character's race. Existing Empire banner retained for humans. Nordic, dwarf, high-elf and wood-elf banners extracted losslessly with alpha masks from the user's heraldry PDF, page 11. Associations checked against the page rendering and image coordinates.
- Worldless avatar widget uses the available preview width instead of fixed `100 * build`; height is fitted conservatively for maximum race height/build. Ear and beard preview calls share the same corrected widget bounds.
- Menu text shadows disabled only on the two Indivis screens. Other Minecraft screens unchanged.
- Colored names, input fields and color-choice labels retain their actual colors with contrasting backgrounds. Name editing, cursor and selection remain vanilla; only the field background draw is redirected.
- Height/build/eye sliders now have contrasting tracks, filled positions and visible handles. Input/ranges/DTO logic unchanged.

## Runtime verification after the final code change

Minecraft 1.21.11; Java 21; Fabric Loader 0.19.5; Fabric API 0.141.6; 3D Skin Layers 1.10.2; Sodium 0.8.7; ImmediatelyFast 1.14.2; software OpenGL/OSMesa. Window 1920×1080.

- Without a world: `PRE28_MENU_PASS captures=23 checks=665`.
- With a copied, isolated test world and Puffish Skills 0.19.0: `PRE28_MENU_PASS captures=23 checks=573`.
- Creation: all five playable races at GUI scales 2, 3 and 4, maximum permitted height/build, actual race-button and name-color-button clicks, black first name plus white last name.
- Selection: all five race fixtures.
- Rotation: a separate post-initialization test verifies the actual widget yaw at 0°, 90° and 180°: `PRE28_MENU_PASS captures=3 checks=93`. Front, side and back captures visually inspected. The original broad fixture requested yaw before widget creation; the separate test avoids this timing issue.
- Actual click/release checks for height, build and eye-height sliders, scrolling into view at larger GUI scales.
- Banner resources present and selected race association asserted on every scene.
- Width, centering and vertical worldless preview bounds asserted after real renders.
- Visual inspection: final captures show complete avatars, correct race banners, readable black/white input fields and name labels, shadow-free button labels and slider tracks.
- PRE27 negative control (same test, unchanged original mixins): expected failure `Avatar width clipped: 115 expected=512`. This confirms the test detects the original clipping bug. The negative control is intentionally not a passing mod candidate.

## Preservation and reproducibility

All **223 original class files** and **4962 original asset files** are byte-identical to PRE27. Only `fabric.mod.json` and the client mixin list differ among existing entries. Four banner PNGs, one helper (plus compiler-generated switch class), and five presentation mixins are added. No changes to skin meshes, UV mapping, skin sourcing, assets, server character logic or palette catalogs.

`assemble.py` reproduces the exact candidate from the immutable PRE27 ZIP and `compiled-patches.zip.b64`; resulting SHA verified above. Delivery ZIP contains this same JAR and no test harness or dependency mods.

The tests cover these menu defects; they are not a blanket certification for every possible asset, graphics driver or external mod combination. Offline test authentication causes expected Mojang/Realms connection warnings, not menu-rendering failures.
