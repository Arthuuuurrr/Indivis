# NexusCharacters Haute Capitale — BETA 1.0 PRE2

Runtime artifact: `NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE2.jar`

Fabric version: `1.0.0-beta.2+hc.1.21.11`

SHA-256: `84dc9b1b38cdef21ee5a429001f66ec5062ab5d348f71acb816a1cb358a78180`

Source bundle: `NexusCharacters-HauteCapitale-BETA-1.0-PRE2-source.zip`

Source SHA-256: `61e1b9117717f2976deccf82fe8a23a346a5b0ff718dce337d757456670eb3b4`

## PRE2 changes

- Worldless beard preview is no longer rendered on the back-facing hemisphere. The beard preview is a separate PlayerSkinWidget, so merely synchronizing yaw could not give it correct occlusion against the main player widget.
- The custom `long` 3D beard has a new chest-length tapered geometry derived from the supplied `barbe longue.png`, substantially longer than `full`.
- Long-hair torso pixels are promoted onto the corresponding vanilla outer/jacket UV layer. This uses the slightly expanded outer model to keep new outfit outer layers from cutting through long hair and removes the observed z-fighting.
- No Nexus persistence or packet schema changes.

## External disconnect diagnosis

The 2026-09-28 disconnect is not a Nexus packet-codec failure. Client stack terminates in `OwoNetChannel`/Endec. At the matching server login, Nexus completes vault installation and `applyCharacterData`, after which entity-NBT loading throws from `AccessoriesHolderImpl.getHolder` through `accessories_compat.trinkets.wrapper.WrappedTrinketComponent.readData`.

The server has `accessories_compat_layer 0.1.13.b1`; the client report does not. Server Cardinal Components is 7.3.2 while the client reports base/entity 7.3.0. Align those dependencies before modifying Nexus networking or deleting character data.

Status: pre-release / in-game validation required.
