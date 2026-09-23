# Haute Capitale — Armor Balance 0.1.1

First proportional balance pass for the existing 40-armor scale.

This is intentionally a compatibility layer rather than edited copies of every armor mod. It scales registered default ARMOR components after items exist, so one file can cover Armory RPGs, MagistuArmory, Nightreign Armor, Hazennstuff, RPG-class armor mods and future modded sets that use normal 1.21.11 item attribute components.

## Rule

Only modifiers matching all of these conditions are doubled:

- item namespace is not `minecraft`;
- attribute is generic `ARMOR`;
- operation is additive `ADD_VALUE`;
- slot is FEET, LEGS, CHEST, HEAD, ARMOR or BODY.

Vanilla armor, toughness, max health, knockback resistance, spell attributes, hand/accessory bonuses and multiplicative armor modifiers are unchanged.

## ARMOR40 mitigation formula

The previous HUD ARMOR40 work raised the attribute cap and rendered the second armor row, but vanilla damage mitigation still capped effective armor at 20.

This module remaps the armor calculation to a 0..40 scale:

- intermediate damage input ×2;
- armor clamp 20 → 40;
- mitigation divisor 25 → 50;
- final damage result ×0.5.

For old modded armor A and new armor 2A, protection remains algebraically equivalent to the old curve, including toughness. This keeps old relative balance while creating room up to 40.

## Runtime audit

Every scaled item is logged:

`[capitale_armor_balance] x2 ARMOR namespace:item [old→new]`

If a modded armor item never appears, that mod supplies armor outside the normal default-item-component path and can be patched separately.

## Artifact

`capitale_armor_balance-0.1.1+1.21.11-ARMOR40-X2-FORMULA.jar`

SHA-256: `53b63960901f9f17f64cf6fb8a1eb9a1a128c2762fddaf90e7bccfd8c34e2571`

Tracked under issue #49. The issue remains open until runtime validation.
