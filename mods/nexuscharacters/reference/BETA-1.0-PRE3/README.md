# NexusCharacters Haute Capitale — BETA 1.0 PRE3

Runtime artifact: `NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE3.jar`

Fabric version: `1.0.0-beta.3+hc.1.21.11`

SHA-256: `4cb9293df6487897804057749b45381b149253572f3918e674e4aec889270540`

Source bundle: `NexusCharacters-HauteCapitale-BETA-1.0-PRE3-source.zip`

Source SHA-256: `327c4d89257f516e7d915a74b9e2c6ad933fb3c2ef6582b7f21991d3b2c958ec`

## Changes

- Merged the 2D facial-hair selector and 3D beard selector into one `Pilosité` button for Human, Nordic and Dwarf.
- Human cycle: 2D facial hair -> open 3D beard.
- Nordic cycle: 2D facial hair -> short/full/open 3D beards.
- Dwarf cycle: short/full/open/forked/braided 3D beards.
- The separate `Barbe naine` button is hidden for bearded races; elf ear selection remains on the original cosmetic button.
- The separate legacy 3D beard-colour button is no longer created.
- Existing facial-hair colour button is the unified colour control and keeps `Liée aux cheveux`.
- Linked mode synchronizes the 3D beard colour to the exact hair-colour palette index.
- Internal beard style id `long` is retained for persistence but its geometry is rebuilt as a shorter, open/patchy beard with visible gaps based on the supplied sparse texture.
- No Nexus network codec, vault or persistence schema change in PRE3.
