# Synergie exclusive au profil bettercombat:spear/two-handed.
scoreboard players set @s CAPSK_LANCE_PHASE 4
effect give @s minecraft:slowness 1 3 true
execute at @s run particle minecraft:electric_spark ~ ~1.0 ~ 0.24 0.20 0.24 0.02 10 force @a[distance=..24]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.35 0.20 0.35 0.01 10 force @a[distance=..24]
execute at @s run playsound minecraft:item.trident.riptide_1 player @a[distance=..24] ~ ~ ~ 0.45 0.72 0
