# Prêt si 20 h de serveur environ se sont écoulées depuis la dernière récupération.
execute store result score #now CAPSK_TIME run time query gametime
scoreboard players operation @s CAPSK_SHADOW_ELAPSED = #now CAPSK_TIME
scoreboard players operation @s CAPSK_SHADOW_ELAPSED -= @s CAPSK_SHADOW_LAST
execute if score @s CAPSK_SHADOW_ELAPSED matches 1440000.. run function capskills:reward/shadow/grant_self
execute unless score @s CAPSK_SHADOW_ELAPSED matches 1440000.. run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre déjà récupérée. Reviens plus tard.","color":"gray"}]
