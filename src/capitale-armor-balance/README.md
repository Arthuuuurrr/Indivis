# Haute Capitale — Armor Balance 0.1.2 SAFE

First proportional balance pass for the existing 40-armor scale.

## 0.1.1 rejected as runtime candidate

After 0.1.1 was installed, magical and martial weapons stopped exposing their Spell Engine abilities even though the CapSkills datapack had not changed. Static comparison ruled out the datapack. The risky part of 0.1.1 was its broad `DefaultItemComponentEvents` predicate: every non-vanilla item, including weapons carrying `spell_engine:spell_container`, entered the component-modification callback before the code decided whether it actually had ARMOR to scale.

0.1.2 keeps the same balance math but narrows the Fabric predicate before the builder callback is ever invoked.

## SAFE item selection

An item enters Armor Balance only when all conditions are already true in its immutable defaults:

- namespace is not `minecraft`;
- it already has `DataComponentTypes.EQUIPPABLE`;
- its equipment slot is `FEET`, `LEGS`, `CHEST`, `HEAD` or `BODY`;
- it already has `ATTRIBUTE_MODIFIERS`.

Therefore staffs, swords, axes, bows, guns, hand items and ordinary accessories never enter the component rewrite path.

Inside actual armor items, only modifiers matching all conditions are doubled:

- attribute is generic `ARMOR`;
- operation is additive `ADD_VALUE`;
- slot is an armor attribute slot.

Vanilla armor, toughness, max health, knockback resistance, spell attributes and multiplicative armor modifiers remain unchanged.

## ARMOR40 mitigation formula

Unchanged from 0.1.1. Vanilla mitigation was still effectively based on the 20-point scale even after the attribute/HUD supported 40. The mixin remaps it to 0..40:

- intermediate damage input ×2;
- armor clamp 20 → 40;
- mitigation divisor 25 → 50;
- final damage result ×0.5.

For old modded armor A and new armor 2A, the old mitigation curve is preserved, including toughness.

## Artifact

`capitale_armor_balance-0.1.2+1.21.11-ARMOR40-X2-SAFE.jar`

SHA-256: `767d542ece17a2d72bf93da7c09e4ac0f5b771c42bfde8fd6101ebb876bbc089`

Runtime validation: remove 0.1.1, install 0.1.2 on client + server, confirm magical/martial weapon spellbars return, then verify representative modded armor remains doubled.

Tracked under issue #49. The issue remains open until runtime validation.
