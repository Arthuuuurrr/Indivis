# NexusCharacters HC 0.8.0-alpha1.3.6 — Outfit Cleanup + Beard Sync

Base runtime: `0.8.0-alpha1.3.5-CURATED-ASSETS-LONG-BEARD-PERF1`.

Runtime JAR:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.6-OUTFIT-CLEANUP-BEARD-SYNC-PERF1.jar`

SHA-256:
`1b29d520f8da9551927bf20601b298bd24d1672a9bd7b36ecbaee89b911dbc7c`

Source ZIP SHA-256:
`13dc23052d31ef1328852cd8de7f0888003af51a8ba932054154149ae8145ce5`

## Outfits

Removed physically and removed from the selector:
- raw slot 2
- raw slot 8
- raw slot 9
- raw slot 11
- raw slot 12

The other raw IDs are not renumbered, so retained saved appearances keep their identity. The UI exposes 33 choices as Tenue 1/33 ... Tenue 33/33. A saved marker referring to a deleted raw outfit normalizes to raw slot 1.

Final audit: 33 runtime outfit PNGs and zero exact SHA-256 duplicates.

## Long 3D beard

The additional `long` 3D beard model remains installed through `LongBeardModelSupport`.
The race-specific selector now exposes it explicitly:
- Human: none -> long -> none
- Nordic: none -> short -> full -> long -> none
- Dwarf: none -> short -> full -> long -> forked -> braided -> short

## Beard / hair color alignment

Hair, dynamic facial hair and the 3D beard base all use grayscale tint masks.
The 3D beard color catalogue is aligned to the 12 exact Appearance69 hair RGB choices: black, brown, chestnut, blond, light_blond, dark_blond, red, bright_red, auburn, gray, white and silver.

Preview beard textures are regenerated from the same grayscale base with the same palette.

## Beard preview rotation

The creation preview renders the player and beard as separate PlayerSkinWidget instances. The beard widget now receives the same yaw state on every render:
`30° + PreviewDragInput.getYawDegrees()`.

This addresses the observed bug where rotating the preview left the beard facing the camera.

## Static validation

- JAR archive integrity OK.
- 33 retained outfit PNGs.
- removed outfit PNGs absent.
- no exact duplicate outfit textures.
- 13 hair masks grayscale.
- 6 facial-hair masks grayscale.
- 3D beard base grayscale.
- long beard model/helper present.
- patched classes parse successfully.

In-game visual validation remains required for the final long-beard geometry and rotation alignment.
