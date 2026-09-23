# Maîtrise de l’arbalète — vélocité standard x1.20.
execute store result entity @s Motion[0] double 0.001 run data get entity @s Motion[0] 1200
execute store result entity @s Motion[1] double 0.001 run data get entity @s Motion[1] 1200
execute store result entity @s Motion[2] double 0.001 run data get entity @s Motion[2] 1200
tag @s add capskills.crossbow_velocity_datapack_boosted
tag @s add capskills.crossbow_velocity_boosted
