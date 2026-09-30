# NexusCharacters HC 0.8.0-alpha1.3.7 — Name Preview + First-Person Skin

Base runtime: `0.8.0-alpha1.3.6-OUTFIT-CLEANUP-BEARD-SYNC-PERF1`.

Runtime JAR:
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.7-NAME-PREVIEW-FIRSTPERSON-SKIN-PERF1.jar`

SHA-256:
`12f054fbeec1a6d7b3038669f5ec06c48ad3c6d2e8eb758dc9de321d13e9a508`

Source ZIP SHA-256:
`8d8c39a785332306d917a49022005da5662c09b6f00c2ce055d3100b8735030e`

## Name-format preview

The responsive character-creation UI renders the selected colour label using the actual Minecraft colour and the selected style label using the actual style. The older CharacterCreationScreen cycle-button path is patched as well.

The existing legacy `&` formatter is reused, so the preview uses the same formatting semantics as character-selection names.

## First-person custom skin

Third-person custom skins were already installed in the player render state. First-person hands use the local `AbstractClientPlayerEntity#getSkin()` path instead, which previously kept the account/default skin.

A client-only mixin now overrides that local skin result when Nexus has an active/selected custom character skin, reusing `RemoteAppearanceSupport`'s existing payload/DTO resolver. Vanilla skin remains the fallback on failure.

## Network report

No Nexus custom-payload codec or packet schema was changed in this build. The reported `clientbound/minecraft:custom_payload` decoder failure cannot be assigned to a specific payload channel from the screenshot alone; the corresponding client `latest.log` is required before treating it as fixed.
