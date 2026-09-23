# 0.9.39 — @s = armor stand totem de guérison.
scoreboard players add @s CAPSK_CHAMAN_PULSE 1
particle minecraft:happy_villager ~ ~0.8 ~ 0.35 0.35 0.35 0.01 3 force @a[distance=..24]
execute if score @s CAPSK_CHAMAN_PULSE matches 40.. run function capskills:skill/chaman/totem_guerison_pulse_as_totem
