# Calcul cylindrique horizontal X/Z : (x-zoneX)^2 + (z-zoneZ)^2 <= rayon².
execute store result score @s CAP_ZONE_X run data get entity @s Pos[0] 1
execute store result score @s CAP_ZONE_Z run data get entity @s Pos[2] 1
scoreboard players operation @s CAP_ZONE_DX = @s CAP_ZONE_X
scoreboard players operation @s CAP_ZONE_DX -= #zone CAP_ZONE_X
scoreboard players operation @s CAP_ZONE_DZ = @s CAP_ZONE_Z
scoreboard players operation @s CAP_ZONE_DZ -= #zone CAP_ZONE_Z
scoreboard players operation @s CAP_ZONE_DX *= @s CAP_ZONE_DX
scoreboard players operation @s CAP_ZONE_DZ *= @s CAP_ZONE_DZ
scoreboard players operation @s CAP_ZONE_DIST2 = @s CAP_ZONE_DX
scoreboard players operation @s CAP_ZONE_DIST2 += @s CAP_ZONE_DZ
execute if score @s CAP_ZONE_DIST2 <= #zone CAP_ZONE_RADIUS2 run tag @s add cap_zone_inside_any
execute if score @s CAP_ZONE_DIST2 <= #zone CAP_ZONE_RADIUS2 if score #zone CAP_ZONE_TYPE matches 1 run tag @s add cap_zone_inside_couronne
execute if score @s CAP_ZONE_DIST2 <= #zone CAP_ZONE_RADIUS2 if score #zone CAP_ZONE_TYPE matches 2 run tag @s add cap_zone_inside_mystere
