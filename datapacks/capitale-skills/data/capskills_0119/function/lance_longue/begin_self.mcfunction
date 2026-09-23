tag @s remove capskills_0119.lance_longue.last_no_target
tag @s remove capskills_0119.lance_longue.active
tag @s add capskills_0119.lance_longue.channeling
scoreboard players set @s CAPSK_LANCE_PHASE 1
scoreboard players set @s CAPSK_LANCE_HITS 0
scoreboard players set @s CAPSK_LANCE_RAY 0
effect give @s minecraft:slowness 1 2 true
execute at @s run particle minecraft:crit ~ ~1.1 ~ 0.35 0.20 0.35 0.01 8 force @a[distance=..20]
execute at @s run playsound minecraft:item.trident.return player @a[distance=..20] ~ ~ ~ 0.55 1.55 0
