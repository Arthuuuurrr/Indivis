scoreboard players set #hit CAPSK_TMP 1
tag @s add capskills.trait_target
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.35 0.45 0.35 0.06 18 force @a[distance=..32]
execute at @s run playsound minecraft:entity.arrow.hit_player player @a[distance=..24] ~ ~ ~ 0.50 1.45 0
