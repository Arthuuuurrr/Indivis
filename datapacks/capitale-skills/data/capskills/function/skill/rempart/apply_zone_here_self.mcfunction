# 0.9.27 : Égide ciblée retirée ; Égide I devient la protection de zone du tank.
# @s = lanceur. Zone défensive teamplay autour du point ciblé ou du lanceur.
effect give @a[distance=..7,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 16 0 true
execute if score @s CAPSK_EGIDE_RANK matches 1.. run effect give @a[distance=..7,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:absorption 18 1 true
execute if score @s CAPSK_EGIDE_RANK matches 1.. run effect give @a[distance=..7,gamemode=!spectator,scores={CAP_PVP=0}] minecraft:resistance 4 0 true
summon minecraft:area_effect_cloud ~ ~0.05 ~ {Radius:7.0f,Duration:160,WaitTime:0,RadiusPerTick:0.0f,Color:16766720}
particle minecraft:effect ~ ~0.12 ~ 6.6 0.03 6.6 0.02 120 force @a[distance=..24]
particle minecraft:enchant ~ ~1 ~ 2.3 0.8 2.3 0.08 55 force @a[distance=..16]
particle minecraft:crit ~ ~1.1 ~ 2.0 0.65 2.0 0.04 26 force @a[distance=..16]
playsound minecraft:block.respawn_anchor.charge player @a[distance=..16] ~ ~ ~ 0.7 1.1 0
scoreboard players set @s CAPSK_REMP_ZONE_CD 35
title @s actionbar {"text":"Égide de zone déployée. Recharge : 35 s.","color":"blue"}
