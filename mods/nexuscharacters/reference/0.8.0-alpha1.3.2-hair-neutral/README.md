# NexusCharacters HC 0.8.0-alpha1.3.2 — Hair Neutral

Runtime artifact:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.2-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-NEUTRAL.jar`

SHA-256:
`02ca53837695758791fda3be64f642bc567129935c34eb49bc0fb8cd62f82969`

Requested base:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.1-COSMETICS-PACK1-PERSISTENCE-PERF1.jar`
(reference SHA-256: `2278108e8be245ab8c574e9c41d7c38537069b53d29bca129ac352d18535f53a`).

The previous PERF1 binary was not mounted in the current runtime, so the build was reproduced from the byte-identical local alpha1.3 Cosmetics Pack 1 base (SHA-256 `512b92cc9796f22487aedd59a7896716f06a4816adcca312cf31ab7453a80da2`) by reapplying PERF1's single persistence optimization before the hair pass.

## Preserved PERF1 behavior

- 100-tick / 5-second persistence checkpoint;
- PuffishSkillsBridge, ProfileStateBridge and PlayerManager saves;
- position and dimension persistence;
- asynchronous persistence queue;
- final logout save;
- shutdown wait for the persistence queue;
- player NBT runtime-tag filtering before GZIP compression instead of compress -> decompress -> filter -> recompress.

## Hair normalization

All 12 `appearance_parts_v064/hair/*.png` textures are converted to neutral grayscale.

Normalization target:
- alpha and UV layout unchanged;
- saturation = 0;
- alpha-weighted mean luminance ~= 172;
- luminance standard deviation ~= 24.

The renderer's HAIR tint uses `0.38 + luminance / 255 * 0.92`. At luminance 172 the multiplier is ~1.0005, so a hairstyle no longer shifts the selected hair RGB because its source texture happened to be brown, blond, purple, darker or lighter.

Only the 12 hair textures, `VaultManager.class` (PERF1 reconstruction), `fabric.mod.json` and two diagnostic metadata files differ from alpha1.3. No outfit, facial-hair, appearance ID, v69 schema, authority or other persistence class is changed.
