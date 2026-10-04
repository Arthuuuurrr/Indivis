# Firearms → Spell Engine hotbar bridge

Issue: #4

## Root cause

The six firearm nodes already existed in CapSkills RC2F, but they only granted
persistent `capskills.fusils.*` player tags. They did **not** grant
`haute_capitale_rpg:abilities` rewards, so `AbilitySync` never inserted any
firearm spell into the server-side Spell Engine container.

## Design

The firearm skills must **not** become Spell Engine projectile implementations.

Spell Engine is used only for:

- visible hotbar slots;
- input/cast lifecycle;
- later cooldown presentation if/when cooldown values are formally defined.

The actual skill is delegated through a custom delivery handler to:

`FusilAPI.useSkill(caster, skillId, 0)`

This preserves the native firearm implementation for burst scheduling, special
ammunition, retreat/reload and Hunter's Mark.

## Six wrappers

| CapSkills node | Spell | Weapon gate |
|---|---|---|
| Repli | `haute_capitale_rpg:firearm_repli` | `capitale:weapon/firearms_skirmish` |
| Tir incapacitant | `haute_capitale_rpg:firearm_tir_incapacitant` | `capitale:weapon/firearms_skirmish` |
| Marque du chasseur | `haute_capitale_rpg:firearm_marque` | `capitale:weapon/firearms_mark` |
| Tir perforant | `haute_capitale_rpg:firearm_tir_perforant` | `capitale:weapon/firearms_piercing` |
| Tir explosif | `haute_capitale_rpg:firearm_tir_explosif` | `capitale:weapon/firearms_explosive` |
| Rafale | `haute_capitale_rpg:firearm_rafale` | `capitale:weapon/firearms_repeater` |

## Builds

Inputs:

- Haute Capitale RPG TEST3 RC2 — SHA-256 `65824c4c54c96825a3ffdc2e8c441c690d9f4a466622efd62b33ff8c6d7548e4`
- haute_capitale_fusils b3 — SHA-256 `0979a7c94e1e959f12b2f8474aee86fc760d3b19a38507c4169b92a79d36e800`
- CapSkills 0.10.19 RC2F — SHA-256 `109ec0428909c009d6599637cc0b23a90c78bd84c9d5772c6447ecf01a3d34f4`

Outputs:

- Haute Capitale RPG TEST3 RC3 FIREARMS SPELLBAR
  - SHA-256 `08f03d4db0c41286851cbd35318b91afcb0e59483a5ce2d7164aa59ebf047e40`
- CapSkills 0.10.20 FIREARMS SPELLBAR BRIDGE
  - SHA-256 `c9b330330bd3cf3f5df5c8c6749acba0d02d18902fbc4e20524b0f9caa6f6ebc`

No cooldown duration is invented in this patch. That remains a separate
balancing/integration decision.
