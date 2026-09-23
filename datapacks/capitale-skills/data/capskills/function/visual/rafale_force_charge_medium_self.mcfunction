# CapSkills 0.9.9 — Rafale low-cost medium halo pulse.
execute at @s positioned ~1.050 ~0.12 ~0.000 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.849 ~0.12 ~0.617 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.324 ~0.12 ~0.999 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.324 ~0.12 ~0.999 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.849 ~0.12 ~0.617 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-1.050 ~0.12 ~0.000 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.849 ~0.12 ~-0.617 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~-0.324 ~0.12 ~-0.999 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.324 ~0.12 ~-0.999 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute at @s positioned ~0.849 ~0.12 ~-0.617 run particle minecraft:enchanted_hit ~ ~ ~ 0.000 0.000 0.000 0.000 1 force @a[distance=..48]
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 24 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:22,Radius:0.75f,RadiusPerTick:0.025f,WaitTime:0,Particle:{type:"minecraft:enchanted_hit"},Tags:["capskills.rafale.visual"]}
