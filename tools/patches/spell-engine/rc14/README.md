# Spell Engine TEST3 RC14 — Item-use spell icon

RC13 targeted the normal spell-icon resolver, but the reported checkerboard is in the separate right-click / vanilla-use-key slot.

## Root cause

`SpellHotbar` creates the held-item use slot with:

- `spell = null`;
- `itemStack = held item`;
- key = vanilla use/right-click.

`HudRenderHelper.lambda$render$0` therefore set `iconId = null` and the widget rendered the raw ItemStack model. RC13's `SpellRender.iconTexture(...)` fallback was never reached.

## RC14

Only the item-use icon branch changes.

When an item-use slot is rendered:

1. resolve `SpellContainerHelper.containerFromItemStack(itemStack)`;
2. if the container is valid/usable and non-empty, take its first spell ID;
3. render it through `SpellRender.iconTexture(...)`;
4. if resolution fails, return null and retain the original ItemStack fallback.

For Berserker axes the native container spell is `more_rpg_classes:decapitate`, whose texture already exists.

## Invariants

- no spell JSON change;
- no cast/input change;
- no cooldown change;
- no impact change;
- no HUD position change;
- normal spell slots retain the existing RC13 path.

Artifact SHA-256:
`6946c10ef60cc02db10aef7c83ba5b7b9ef0c0f4d29da46bcf8805560670d9ea`
