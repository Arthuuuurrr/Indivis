# Soin de zone : limité aux joueurs hors PvP pour éviter l'aide automatique à un adversaire.
effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:regeneration 8 0 true
execute if score @s CAPSK_SEC_RANK matches 2.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 22 0 true
execute if score @s CAPSK_SEC_RANK matches 3.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:regeneration 8 1 true
execute if entity @s[tag=capskills.support.coordination.r1] run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:speed 5 0 true
execute if score @s CAPSK_SUPPORT_RANK matches 1.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:speed 8 0 true
execute if score @s CAPSK_SUPPORT_RANK matches 2.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 14 0 true
execute if score @s CAPSK_SUPPORT_RANK matches 3.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:resistance 4 0 true
execute if score @s CAPSK_MAG_SUP_RANK matches 1.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 10 0 true
execute if score @s CAPSK_MAG_SUP_RANK matches 2.. run effect clear @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:poison
execute if entity @s[tag=capskills.support.purification.r1] run effect clear @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:slowness
execute if entity @s[tag=capskills.support.purification.r1] run effect clear @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:mining_fatigue
execute if score @s CAPSK_MAG_SUP_RANK matches 2.. run effect give @a[distance=..8,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 14 0 true
summon minecraft:area_effect_cloud ~ ~0.05 ~ {Radius:8.0f,Duration:160,WaitTime:0,RadiusPerTick:0.0f,Color:16262179,potion_contents:{custom_color:16262179},Particle:{type:"minecraft:instant_effect"}}
particle minecraft:instant_effect ~ ~0.12 ~ 7.6 0.03 7.6 0.02 220 force @a[distance=..24]
particle minecraft:heart ~ ~1 ~ 2.2 0.8 2.2 0.08 45 force @a[distance=..20]
particle minecraft:happy_villager ~ ~1.1 ~ 2.0 0.7 2.0 0.04 24 force @a[distance=..20]
particle minecraft:composter ~ ~0.25 ~ 3.6 0.05 3.6 0.08 60 force @a[distance=..20]
playsound minecraft:block.beacon.power_select player @a[distance=..16] ~ ~ ~ 0.7 1.5 0
scoreboard players set @s CAPSK_ZONE_CD 30
title @s actionbar {"text":"Cercle de secours déployé.","color":"green"}
