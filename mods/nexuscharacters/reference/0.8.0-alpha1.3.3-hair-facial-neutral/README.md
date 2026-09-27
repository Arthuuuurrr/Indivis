# NexusCharacters HC 0.8.0-alpha1.3.3 — Hair + Facial Hair Neutral

Base runtime: `0.8.0-alpha1.3.2-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-NEUTRAL`.

This pass keeps the 12 hairstyle masks from alpha1.3.2 unchanged and applies the same neutral-grayscale normalization to the five dynamic facial-hair masks in `appearance_parts_v068/facial_hair/`.

Target: saturation 0, alpha-weighted luminance mean 172, standard deviation 24. Alpha/UV geometry is preserved exactly.

The legacy 3D beard renderer (`textures/cosmetic/beard_base.png` plus pre-colored `beard_*.png`) is intentionally not changed: it uses a separate direct-tint / legacy-color path and normalizing those files with the dynamic-mask formula would alter existing legacy beard colors.

Runtime JAR: `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.3-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-FACIAL-NEUTRAL.jar`
SHA-256: `cd1101b5efc5792e274cd780b8b76a04e16755bd8ddfc7b2e273045f6d5992be`

Source ZIP SHA-256: `2a3ff2c6d27cfa13be4446b6c5e6894381bdadc87827c5df252145cd1b4ed6d7`
