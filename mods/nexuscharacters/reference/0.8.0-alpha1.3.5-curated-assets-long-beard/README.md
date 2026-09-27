# NexusCharacters HC 0.8.0-alpha1.3.5 — Curated Assets + Long Beard

Base runtime: `0.8.0-alpha1.3.4-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-FACIAL-NEUTRAL-NAME-COLORS`.

Runtime JAR:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.5-CURATED-ASSETS-LONG-BEARD-PERF1.jar`

SHA-256:
`e024fcde969ad9a7593619f019490d070cbd2e5ffa36772f8e4a9e5193ef46a8`

Source bundle SHA-256:
`5360b98e725310cf7a458d45511dccc0901439bb24c55a70c7a34aa80a76556b`

Input hashes:
- barbecheveux.zip: `7e11b04df12ea0ff57d24aee683e091fff0c789c04a55ed8f773d0dde73c31d2`
- clothes.zip: `b438017fbc19fad7b6eef180187f42ba61aac2cb4bcd9a1b67164d5c5ec28f72`
- basicbody(2).png: `e809715ad9bf4b181b1de6c7203592a7d6eff8f13c6850ba478e0c6f958e554c`

## Integration

- 13 selectable hair assets; slot 13 is the supplied calvitie.
- Hair masks are normalized to the shared grayscale tint basis.
- 6 dynamic facial-hair assets; the supplied second thin beard is a new slot.
- Thin beards and moustaches are selectable only for Human and Nordic characters.
- 38 outfit slots.
- Exact duplicate `armure3.png == côte de maille1.png` is not kept twice: slot 33 keeps Côte de maille 1 and the freed slot 35 is reused for the new Armure 1.
- New outfits: 35 Armure 1, 36 Pirate, 37 Voyageur 1, 38 Apothicaire.
- The old king-clothes content in slot 20 is replaced by the supplied `outfit_20.png`; `kingclothes.png` is not shipped.
- New 3D beard style `long`, with a tapered geometry derived from the supplied `barbe longue.png` silhouette.
- Long beard: Human + Nordic + Dwarf.
- Human 3D beard cycle: none/long.
- Nordic 3D beard cycle: none/short/full/long.
- Dwarf keeps previous styles and gains long.
- Persistence PERF1 and formatted character-name rendering are preserved.

Static validation: JAR integrity OK, modified bytecode parses with javap, 13 hair resources, 6 facial-hair resources, 38 outfit resources, no exact duplicate runtime outfits, saturation 0 on all tintable hair/facial masks.

Runtime Minecraft validation is still required for the new long-beard 3D alignment.
