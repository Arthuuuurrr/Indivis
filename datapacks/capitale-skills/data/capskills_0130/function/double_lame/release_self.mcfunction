tag @s remove capskills_0130.double_lame.channeling
tag @s add capskills_0130.double_lame.active
scoreboard players set @s CAPSK_EXEC_CD 20
# La préparation est désormais très courte : on retire immédiatement le ralentissement et on donne une impulsion de mobilité.
effect clear @s minecraft:slowness
effect give @s minecraft:speed 2 1 true
execute at @s run particle minecraft:smoke ~ ~0.25 ~ 0.18 0.08 0.18 0.01 8 force @a[distance=..24]
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 0.65 1.55 0
