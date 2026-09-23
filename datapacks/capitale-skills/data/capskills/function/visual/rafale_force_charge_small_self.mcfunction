# CapSkills 0.9.9 — Rafale low-cost small halo pulse.
execute at @s positioned ~0.650 ~0.10 ~0.000 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.460 ~0.10 ~0.460 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.000 ~0.10 ~0.650 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.460 ~0.10 ~0.460 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.650 ~0.10 ~0.000 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.460 ~0.10 ~-0.460 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.000 ~0.10 ~-0.650 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.460 ~0.10 ~-0.460 run particle minecraft:effect ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 1 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:18,Radius:0.35f,RadiusPerTick:0.030f,WaitTime:0,Particle:{type:"minecraft:effect"},Tags:["capskills.rafale.visual"]}
