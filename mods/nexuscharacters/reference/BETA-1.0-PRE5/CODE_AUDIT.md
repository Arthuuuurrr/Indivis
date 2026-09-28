# BETA 1.0 PRE5 — Code audit

Audit performed against the Git/LFS runtime identified by:

- SHA-256: `a584b35333152bcdf6dfbd1f8f65a4ef4d7cac935bf59898869efdf7d4621ac3`
- size: `21418633` bytes
- Fabric version: `1.0.0-beta.5+hc.1.21.11`

## Structural checks

- JAR ZIP integrity: PASS.
- 179 Nexus class files parsed successfully with ASM CheckClassAdapter: PASS.
- PRE5 helper/reference sources compile against the runtime JAR: PASS.
- PRE5 delta versus PRE4 is limited to:
  - `LongBeardModelSupport.class`
  - `RacialAppearance69Support.class`
  - `UnifiedPilositySupport.class`
  - `CharacterCreationAppearance65Mixin.class`
  - metadata/version files.

## UI / beard logic tests

Synthetic creation-screen tests:

- Human unified pilosity cycle: PASS.
- Nordic unified pilosity cycle: PASS.
- Dwarf unified 3D-beard cycle: PASS.
- Dwarf selector visibility/activation: PASS.
- Transition from 2D facial hair to 3D beard label: PASS.
- Hair-linked beard colour selection: PASS.
- Ornament button visibility on 3D beard: PASS.

Codec/restriction matrix: 448 race/style/ornament combinations checked: PASS.

## Network / persistence regression check

The following classes are byte-identical to PRE4:

- `CharacterAppearancePayload`
- `CharacterCosmeticPayload`
- `VaultChunkS2CPayload`
- `SaveAckPayload`
- `SelectCharacterPayload`
- `CharacterDto`

PRE5 therefore does not introduce a packet-schema or DTO-schema change.

## Non-blocking findings

1. `META-INF/haute_capitale/NEXUS_BETA_1_0_PRE4.txt` is still present as stale build metadata in PRE5. It is not executed.
2. A later local working-copy JAR had a different SHA because its PRE4/PRE5 metadata files differed, but its class files were identical to the published Git/LFS PRE5 runtime.
3. Several compatibility helpers intentionally catch `Throwable` and ignore it. This protects the UI from hard crashes but can hide future mapping/reflection regressions; runtime logs/tests remain necessary.

Conclusion: no blocking bytecode or deterministic PRE5 beard/UI logic defect was found in this audit. In-game validation is still required for rendering geometry, Mixin application in the full modpack, and interaction with other mods.
