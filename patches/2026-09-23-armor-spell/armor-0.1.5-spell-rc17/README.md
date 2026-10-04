# Armor40 hybrid 0.1.5 + Spell Engine RC17

## Why 0.1.4 failed functionally

0.1.4 removed the DefaultItemComponentEvents path that had already been proven to double vanilla armor in 0.1.2. Its replacement hooks on AttributeModifiersComponent apply methods were not part of the path that constructs the effective stored armor component, so vanilla returned to base values and Armory remained unchanged.

## Armory / RPG Series root cause

Armory defines its actual RPG armor values through ArmorSetConfig.Piece, then delegates registration to Spell Engine Armor.register(...).

Spell Engine's private Armor.attributesFrom(...) creates a fresh AttributeModifiersComponent and injects piece.armor directly into GENERIC_ARMOR / ADD_VALUE. This happens after the earlier default component seen by Armor Balance 0.1.2, so it overwrites that earlier x2 result.

## Final design

### Armor Balance 0.1.5

Return to the exact 0.1.2 implementation:
- DefaultItemComponentEvents x2 pass;
- only GENERIC_ARMOR + ADD_VALUE + armor slots;
- vanilla restored to x2;
- standard modded armor still covered;
- Armor40 damage formula unchanged.

Only fabric.mod.json differs from 0.1.2.

Artifact:
capitale_armor_balance-0.1.5+1.21.11-ARMOR40-HYBRID-RPG.jar

SHA-256:
b26d96e2a532dff586e9a2d4e6fb14d12238d3639783c8c6bc0dd55597bf1019

### Spell Engine RC17

RC17 is RC16 plus one bytecode change in net.spell_engine.rpg_series.item.Armor.attributesFrom(...).

Immediately after the second read of ArmorSetConfig.Piece.armor — the value passed into EntityAttributeModifier — RC17 inserts:

ICONST_2
IMUL

Therefore only the configured RPG armor point value is doubled.

Unchanged:
- armor toughness;
- knockback resistance;
- selected RPG attributes;
- weapons;
- spell logic;
- cooldowns;
- damage;
- HUD spell restoration;
- Provocation icon.

Artifact:
spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC17-RPG-ARMOR-X2.jar

SHA-256:
64e31c2239b2e341c5ae514ca522814f36549c819fa88a7e7bf7a5186bc75fbd

## Expected Armory values

Justicar: 3/8/6/3 -> 6/16/12/6 = 40 total.
Destroyer: 3/8/6/3 -> 6/16/12/6 = 40 total.
Deathmantle: 2/4/4/2 -> 4/8/8/4 = 24 total.
Strider: 2/4/4/2 -> 4/8/8/4 = 24 total.
Caster robes: 1/3/2/1 -> 2/6/4/2 = 14 total.

## Static validation

RC16 -> RC17 changed only:
- fabric.mod.json
- net/spell_engine/rpg_series/item/Armor.class

HudRenderHelper SHA-256 unchanged:
1878831e5530337a5436987fb6446c2b3bd17197869f021d50a7d56ef8f1892f

Provocation icon SHA-256 unchanged:
680c5ab0aab873b5b488b059ad92cd340ae22b694d8d2f5a0b171d62b7e6ad25

Armor 0.1.2 -> 0.1.5 changed only:
- fabric.mod.json

Armor main implementation SHA-256 unchanged:
1a53a50aac8eb17eb07b20de89488c986c9670f4a5e9f8eeb4837b78668643eb

Armor40DamageFormulaMixin SHA-256 unchanged:
122a0aa8ce60964613a08cd069bca8d8c5f0eb26a0770430da3bdac2c3a194c4

Both JARs pass ZIP integrity checks.

Do not promote to artifacts/jars until runtime validation.
