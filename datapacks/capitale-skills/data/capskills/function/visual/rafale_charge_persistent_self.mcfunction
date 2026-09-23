# CapSkills BETA 0.9.8 — visuel de canalisation Rafale non intrusif.
# Halo bas au sol uniquement ; aucune particule placée devant les yeux.
execute at @s run particle minecraft:effect ~ ~0.18 ~ 0.55 0.04 0.55 0.010 10 force @a[distance=..48]
execute at @s run particle minecraft:enchanted_hit ~ ~0.22 ~ 0.75 0.04 0.75 0.010 8 force @a[distance=..48]
execute if score @s CAPSK_RAFALE_VIS matches 1 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:18,Radius:0.35f,RadiusPerTick:0.045f,WaitTime:0,Particle:{type:"minecraft:effect"},Tags:["capskills.rafale.visual"]}
execute if score @s CAPSK_RAFALE_VIS matches 18 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:22,Radius:0.75f,RadiusPerTick:0.035f,WaitTime:0,Particle:{type:"minecraft:enchanted_hit"},Tags:["capskills.rafale.visual"]}
execute if score @s CAPSK_RAFALE_VIS matches 36 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:26,Radius:1.15f,RadiusPerTick:0.025f,WaitTime:0,Particle:{type:"minecraft:end_rod"},Tags:["capskills.rafale.visual"]}
execute if score @s CAPSK_RAFALE_VIS matches 56 run summon minecraft:area_effect_cloud ~ ~0.05 ~ {Duration:30,Radius:1.55f,RadiusPerTick:0.020f,WaitTime:0,Particle:{type:"minecraft:end_rod"},Tags:["capskills.rafale.visual"]}
execute if score @s CAPSK_RAFALE_VIS matches 56 at @s run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.35 1.80 0
