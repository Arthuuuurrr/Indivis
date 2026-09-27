# NexusCharacters HC 0.8.0-alpha1.3.4 — Name Colors

Base: `0.8.0-alpha1.3.3-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-FACIAL-NEUTRAL`.

Changes:
- labels: `Moustache 1` -> `Moustache mince`, `Barbe légère` -> `Barbe mince`;
- character-selection list parses classic Minecraft `&` formatting codes (`&0`..`&f`, `&k`..`&o`, `&r`) and section-sign equivalents;
- the `  —  Race` suffix is rendered with default formatting, so the final name formatting does not leak into the race label.

Example:
`&4Arthur &4&lPendragon` -> dark-red `Arthur ` + dark-red bold `Pendragon`.

No persistence, vault, server-authority, appearance-ID or texture logic was changed in this pass.

Runtime JAR:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.4-COSMETICS-PACK1-PERSISTENCE-PERF1-HAIR-FACIAL-NEUTRAL-NAME-COLORS.jar`

SHA-256:
`44f15870c72e129b5fdb6ddc481a8e16a6e0eade9c345ef7860b847baa5aaefd`
