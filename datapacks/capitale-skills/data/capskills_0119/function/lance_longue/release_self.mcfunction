tag @s remove capskills_0119.lance_longue.channeling
tag @s add capskills_0119.lance_longue.active
scoreboard players set @s CAPSK_LANCE_PHASE 2
scoreboard players set @s CAPSK_LANCE_CD 22
effect give @s minecraft:slowness 2 1 true
execute at @s run playsound bettercombat:spear_stab player @a[distance=..24] ~ ~ ~ 0.65 1.10 0
