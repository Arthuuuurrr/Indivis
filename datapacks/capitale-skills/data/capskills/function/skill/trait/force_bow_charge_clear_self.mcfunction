# CapSkills BETA 0.9.8 — nettoyage visuel forcé Rafale.
tag @s remove capskills.trait.rafale.force_visual
scoreboard players set @s CAPSK_RAFALE_FORCE_VIS 0
execute at @s run kill @e[type=minecraft:area_effect_cloud,tag=capskills.rafale.visual,distance=..3]
tag @s remove capskills.trait.rafale.visual_channel
scoreboard players set @s CAPSK_RAFALE_VIS 0
