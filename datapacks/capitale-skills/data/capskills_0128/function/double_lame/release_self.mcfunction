tag @s remove capskills_0128.double_lame.channeling
tag @s add capskills_0128.double_lame.active
scoreboard players set @s CAPSK_EXEC_CD 18
effect give @s minecraft:speed 2 0 true
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 0.65 1.35 0
