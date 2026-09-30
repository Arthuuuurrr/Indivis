# Nexus Capitale First Spawn Bridge 1.0.0

Server-side Fabric 1.21.11 compatibility bridge.

## Purpose

NexusCharacters creates a genuinely empty server-authoritative vault for a newly-created character,
but the old RP-HUD integration that called
`capitale:spawn/character_first_join_to_prologue_start_self` no longer owns character slots.

The bridge hooks the exact Nexus method that creates an empty authoritative vault:
`ServerAuthorityV080.createEmptyAuthoritativeVault(Path, UUID)`.

Only in that case, on the corresponding player's JOIN, it waits 3 server ticks then executes as the
player:

`function capitale:spawn/character_first_join_to_prologue_start_self`

Existing character vaults do not pass through this hook and therefore are not restarted.

This is intentionally server-only and does not alter Nexus persistence, character data, scores,
quests, Creature Bundle, Orc logic, or EasyNPC.
