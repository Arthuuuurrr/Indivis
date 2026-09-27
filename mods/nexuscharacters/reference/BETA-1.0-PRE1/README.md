# NexusCharacters Haute Capitale — BETA 1.0 PRE1

This is the first BETA 1.0 pre-release and replaces the alpha-style version naming for active development.

Runtime artifact:
`NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE1.jar`

Fabric version:
`1.0.0-beta.1+hc.1.21.11`

SHA-256:
`bdbef6af729490efd3cbed31030d97d45bb73162c1aa2e7319831b814964668a`

Source bundle:
`NexusCharacters-HauteCapitale-BETA-1.0-PRE1-source.zip`

Source bundle SHA-256:
`44f2b23a693c3181945d2b5b295afec5672c77bf816f773268fc49c68458a392`

## Code state

BETA 1.0 PRE1 is a versioning promotion of the alpha1.3.7 implementation. No gameplay, persistence, appearance or network behavior changed during the promotion itself.

It therefore includes the current work:
- curated character-creation assets;
- 33-outfit selector;
- 13 hairstyles;
- grayscale hair/facial-hair tint basis;
- long 3D beard model and preview rotation synchronization;
- Persistence PERF1;
- Minecraft-format character names;
- live colour/style preview controls;
- local first-person custom-skin synchronization.

## Validation status

Status: **pre-release / testing**.

Before promotion to BETA 1.0 final, validate:
- outfit selector and removed outfit IDs;
- long beard geometry and rotation;
- beard/hair colour consistency;
- colour/style name previews;
- first-person arm skin;
- old-character `clientbound/minecraft:custom_payload` decoder error.

Do not treat this PRE1 as deployed until client and server installation is confirmed.
