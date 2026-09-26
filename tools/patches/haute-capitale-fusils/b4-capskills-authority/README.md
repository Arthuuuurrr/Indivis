# Fusils b4 — CapSkills server authority

Fixes #3 without closing it.

## Root cause

The native firearm packet path reached `GunplayManager.tryFire(...)` without consulting CapSkills. `HunterSkills.use(...)` likewise accepted the six native skills without checking the corresponding `capskills.fusils.*` tags.

## Authority model

- `classe_fusilier` grants `capskills.fusils.use`.
- A server-side player needs that base tag for every normal firearm shot.
- A native firearm skill additionally needs `capskills.fusils.<skillId>`.
- Client prediction is not hard-blocked; the server remains authoritative.
- Non-player API users are preserved.
- Damage, ammo, reload and burst logic are unchanged.

## Outputs

- `haute_capitale_fusils-0.1.0+1.21.11.b4-CAPSKILLS-AUTHORITY.jar`
  - SHA-256 `eff4d5451ebe7d660fbc2acb640c48b1a2028591d934610c66e5c353b28db94c`
- `capitale_skills_BETA_0_10_21_FIREARMS_SERVER_AUTHORITY.zip`
  - SHA-256 `dde382493a8598ec0f2bfcf13d0b05798b18c62da0761659a3595dcb4b83b6f6`

The 0.10.21 datapack includes all 0.10.20 Spell Engine firearm-bar integration.
