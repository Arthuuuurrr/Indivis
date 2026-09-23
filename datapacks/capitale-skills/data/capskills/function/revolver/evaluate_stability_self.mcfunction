# CapSkills 0.9.152 — posture mesurée uniquement lors du tir.
tag @s remove capskills.revolver.steady
scoreboard players set @s CAPREV_MOTION_X 999999
scoreboard players set @s CAPREV_MOTION_Z 999999
execute store result score @s CAPREV_MOTION_X run data get entity @s Motion[0] 1000
execute store result score @s CAPREV_MOTION_Z run data get entity @s Motion[2] 1000
execute if score @s CAPREV_MOTION_X matches -20..20 if score @s CAPREV_MOTION_Z matches -20..20 if entity @s[nbt={OnGround:1b}] run tag @s add capskills.revolver.steady
