# Baguette d’Altération 0.8.10 : Préparatoire = expédition ; Instable = contrôle offensif non-joueur.
effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:saturation 1 0 true
execute if entity @s[tag=capskills.alteration.stabilisation.r1] run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:slow_falling 8 0 true
execute if score @s CAPSK_ALCH_RANK matches 2.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:fire_resistance 35 0 true
execute if score @s CAPSK_ALCH_RANK matches 3.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:slow_falling 20 0 true
execute if entity @s[tag=capskills.alchimie.instable.r1] run particle minecraft:smoke ~ ~1 ~ 1.2 0.45 1.2 0.01 12 force @a[distance=..16]
execute if entity @s[tag=capskills.alchimie.instable.r1] run particle minecraft:witch ~ ~1 ~ 2.0 0.75 2.0 0.05 34 force @a[distance=..16]
execute if entity @s[tag=capskills.alchimie.instable.r1] as @e[type=#capskills:tauntable,distance=..6] run effect give @s minecraft:slowness 5 0 true
execute if entity @s[tag=capskills.alchimie.instable.r1] if entity @s[tag=capskills.alteration.formules.r1] as @e[type=#capskills:tauntable,distance=..6] run effect give @s minecraft:slowness 6 0 true
execute if entity @s[tag=capskills.alchimie.instable.r1] if entity @s[tag=capskills.alteration.formules.r1] as @e[type=#capskills:tauntable,distance=..6] run effect give @s minecraft:glowing 4 0 true
execute if entity @s[tag=capskills.alchimie.instable.r1] as @e[type=#capskills:tauntable,distance=..6] run effect give @s minecraft:weakness 4 0 true
execute if entity @s[tag=capskills.alchimie.instable.r2] as @e[type=#capskills:tauntable,distance=..6] run effect give @s minecraft:poison 4 0 true
summon minecraft:area_effect_cloud ~ ~0.05 ~ {Radius:8.0f,Duration:160,WaitTime:0,RadiusPerTick:0.0f,Color:65280}
particle minecraft:effect ~ ~0.12 ~ 7.6 0.03 7.6 0.02 140 force @a[distance=..24]
particle minecraft:effect ~ ~1 ~ 2 0.7 2 0.04 30 force @a[distance=..16]
particle minecraft:enchanted_hit ~ ~1 ~ 1.8 0.6 1.8 0.03 18 force @a[distance=..16]
playsound minecraft:block.brewing_stand.brew player @a[distance=..16] ~ ~ ~ 0.8 0.9 0
scoreboard players set @s CAPSK_ALCH_CD 45
title @s actionbar {"text":"Baguette d’Altération diffusée.","color":"green"}
