# Contexte : @s = marker de zone. On stocke sa position dans des scores temporaires globaux.
# Type 1 = grande zone capitale/Couronne, type 2 = périmètre mineur plus mystérieux.
execute if score @s CAP_ZONE_RADIUS matches 800 run tag @s add cap_zone_capitale
execute unless score @s CAP_ZONE_RADIUS matches 800 run tag @s add cap_zone_mystere
scoreboard players set #zone CAP_ZONE_TYPE 2
execute if score @s CAP_ZONE_RADIUS matches 800 run scoreboard players set #zone CAP_ZONE_TYPE 1
execute store result score #zone CAP_ZONE_X run data get entity @s Pos[0] 1
execute store result score #zone CAP_ZONE_Z run data get entity @s Pos[2] 1
scoreboard players operation #zone CAP_ZONE_RADIUS2 = @s CAP_ZONE_RADIUS2
execute at @s as @a[distance=..1000] run function capitale:zone/protection/check_player_against_marker_self
