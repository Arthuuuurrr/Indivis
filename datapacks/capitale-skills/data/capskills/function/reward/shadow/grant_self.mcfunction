# Cache de l’Ombre : perles + potion custom, puis mémorisation du temps serveur.
execute store result score #now CAPSK_TIME run time query gametime
scoreboard players operation @s CAPSK_SHADOW_LAST = #now CAPSK_TIME
tag @s add capskills.shadow.kit.claimed
execute if entity @s[tag=capskills.ombre.cache.r2] run give @s minecraft:ender_pearl 32
execute unless entity @s[tag=capskills.ombre.cache.r2] if entity @s[tag=capskills.ombre.cache.r1] run give @s minecraft:ender_pearl 16
execute if entity @s[tag=capskills.ombre.cache.r2] run function capskills:reward/shadow/potion_ombre_renforcee_self
execute unless entity @s[tag=capskills.ombre.cache.r2] if entity @s[tag=capskills.ombre.cache.r1] run function capskills:reward/shadow/potion_ombre_self
execute at @s run playsound minecraft:block.sculk_sensor.clicking player @s ~ ~ ~ 0.65 0.75 0
execute if entity @s[tag=capskills.ombre.cache.r2] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre II récupérée : 32 perles et Potion de l’Ombre renforcée.","color":"dark_gray"},{"text":" Prochaine récupération : environ 20 h de serveur.","color":"gray"}]
execute unless entity @s[tag=capskills.ombre.cache.r2] if entity @s[tag=capskills.ombre.cache.r1] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre I récupérée : 16 perles et Potion de l’Ombre.","color":"dark_gray"},{"text":" Prochaine récupération : environ 20 h de serveur.","color":"gray"}]
