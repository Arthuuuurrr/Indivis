# 0.9.39 — @s = armor stand totem de protection.
scoreboard players add @s CAPSK_CHAMAN_PULSE 1
particle minecraft:enchant ~ ~0.8 ~ 0.45 0.45 0.45 0.01 4 force @a[distance=..24]
execute if score @s CAPSK_CHAMAN_PULSE matches 40.. run function capskills:skill/chaman/totem_protection_pulse_as_totem
