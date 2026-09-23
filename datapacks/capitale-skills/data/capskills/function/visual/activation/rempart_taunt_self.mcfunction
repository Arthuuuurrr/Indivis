# CapSkills 0.9.9 — activation Rempart / Provocation : onde rouge basse.
function capskills:visual/taunt_ring_self
execute at @s run particle minecraft:angry_villager ~ ~1.35 ~ 1.40 0.45 1.40 0.02 18 force @a[distance=..32]
execute at @s run particle minecraft:flame ~ ~0.15 ~ 1.30 0.03 1.30 0.00 14 force @a[distance=..32]
