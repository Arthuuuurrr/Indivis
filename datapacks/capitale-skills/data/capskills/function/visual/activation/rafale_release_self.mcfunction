# CapSkills 0.9.11 — activation Rafale : signal de départ clair, syntaxe particule sûre 1.21.11.
execute at @s run particle minecraft:sonic_boom ~ ~1.1 ~ 0.08 0.12 0.08 0.00 1 force @a[distance=..48]
execute at @s run particle minecraft:end_rod ~ ~0.25 ~ 1.10 0.02 1.10 0.00 18 force @a[distance=..48]
execute at @s run particle minecraft:crit ~ ~1.15 ~ 0.35 0.35 0.35 0.02 14 force @a[distance=..48]
execute at @s run playsound minecraft:item.crossbow.quick_charge_3 player @a[distance=..32] ~ ~ ~ 0.45 1.30 0
