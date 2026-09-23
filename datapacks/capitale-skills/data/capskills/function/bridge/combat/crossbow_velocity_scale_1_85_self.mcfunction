# CapSkills 0.9.22 — vélocité Salve préparée x1.85.
# NBT Motion est multiplié composante par composante.
execute store result entity @s Motion[0] double 0.001 run data get entity @s Motion[0] 1850
execute store result entity @s Motion[1] double 0.001 run data get entity @s Motion[1] 1850
execute store result entity @s Motion[2] double 0.001 run data get entity @s Motion[2] 1850
tag @s add capskills.crossbow_velocity_datapack_boosted
tag @s add capskills.crossbow_velocity_boosted
