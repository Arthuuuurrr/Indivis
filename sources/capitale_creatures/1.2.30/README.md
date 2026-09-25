# Capitale Creatures 1.2.30 / BETA 0.24

## Marine AI consolidation

This release removes competing marine movement writers from the active Fabric entrypoints.

Disabled as entrypoints:
- CapitaleCreaturesMarineSystem120
- CapitaleCreaturesMarineLocomotion1210
- CapitaleCreaturesMarineCombat129

Active marine layers:
- CapitaleCreaturesAquaticPopulation129: population/spawn
- CapitaleCreaturesAquaticBehavior128: Thornshell-only behavior support
- CapitaleCreaturesMarineCombat130: single pursuit/combat authority for simple marine hostiles

## Direct-controlled mobs
- aquatic Dino Mounts Aegirocassis / Dunkleosteus / Mosasaurus variants
- chaos_mmo_ai:corpsefish
- chaos_mmo_ai:gluttonfish
- cubeanimals:piranha

## Specialized AI
All myths_of_the_sea:* mobs keep their native movement/combat AI.
The Kraken gets buoyancy-only assistance:
- no gravity while touching water
- small upward correction when grounded
- strong downward velocity capped
- horizontal native velocity and native attacks are untouched

## Other safeguards
- dead, spectator and creative players are ignored
- 20 tick target memory smooths brief water-contact loss
- forward water probe adds deterministic vertical/lateral obstacle avoidance
- no random wandering is introduced during chase
- melee engagement ranges are slightly tighter than 1.2.29

## Spawn data
BETA 0.24 keeps all BETA 0.23 spawn/balance data unchanged.

SHA-256:
- Bundle 1.2.30: 137289be89108c8f371c9d65175728656f6e8e3a514487137f017ca3cf402aec
- BETA 0.24: 13722c1b5e2a5c69bab9a753efda444879a133080a737875bc5f10729b9aa6cb
