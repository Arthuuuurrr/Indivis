# CapSkills 0.9.22 — vélocité arbalète standard x1.35.
# NBT Motion est multiplié composante par composante.
execute store result entity @s Motion[0] double 0.001 run data get entity @s Motion[0] 1350
execute store result entity @s Motion[1] double 0.001 run data get entity @s Motion[1] 1350
execute store result entity @s Motion[2] double 0.001 run data get entity @s Motion[2] 1350
tag @s add capskills.crossbow_velocity_datapack_boosted
tag @s add capskills.crossbow_velocity_boosted
