# Issue #15 — Race health vs equipment MAX_HEALTH modifiers

The race system must own only the **base** of `MAX_HEALTH`. Equipment and effects are allowed to add modifiers on top of that base.

## Audit result

The existing stack was still incompatible with bonus hearts:

- HUD `CapitaleExactPersonnagesAutoSync` forced total max health back to 18/20/22 by compensating the base for existing modifiers.
- HUD `CapitaleStrictRaceHealthFinalizer` compared total max health to the racial base and normalized current health against that base.
- HUD `CapitaleRebuildMorphServer` periodically cleared `health_boost` and `absorption`.
- NexusCharacters `HauteCapitaleIdentity` preserved the correct race base but clamped current health to `CharacterRace.maxHealth()` instead of the actual total max health.
- Hazennstuff legitimately supplies MAX_HEALTH equipment modifiers, so these clamps break real equipment bonuses.

## Fixed outputs

- `capitale_rp_hud_BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT.jar`
  SHA-256 `f12179dd3f10d6870d28ad864e01f0e16bd5c9ef5156a346c77f3e10809de145`
- `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2-HEALTH-MODIFIER-COMPAT.jar`
  SHA-256 `ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1`

Harness: base 20 + equipment +4 => total 24 and current 24 stays 24. Dwarf base 22 + equipment +4 => total 26.
Issue remains open until runtime validation.
