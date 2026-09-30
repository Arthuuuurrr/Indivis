# Network diagnosis — 2026-09-28

Client:
- `DecoderException: Failed to decode packet clientbound/minecraft:custom_payload`.
- Cause: `OwoNetChannel` attempts to resolve a numeric message entry whose Endec is missing.

Server at the same login:
- Nexus vault install succeeds.
- Nexus `applyCharacterData` completes.
- Immediately afterwards entity NBT loading fails with a null Accessories capability in `AccessoriesHolderImpl.getHolder`, called from `accessories_compat.trinkets.wrapper.WrappedTrinketComponent.readData`.
- The player disconnects in the same second.

Version mismatch found:
- server: Accessories 1.4.3-beta + Accessories Compatibility Layer 0.1.13.b1 + Trinkets 3.11.0-beta.2 + Cardinal Components 7.3.2;
- client: Accessories 1.4.3-beta + Trinkets 3.11.0-beta.2 + no Accessories Compatibility Layer in the crash-report mod list + Cardinal Components base/entity 7.3.0.

Do not delete Nexus vaults based on this failure. Back up world/vault data before any Accessories/Trinkets migration or cleanup.
