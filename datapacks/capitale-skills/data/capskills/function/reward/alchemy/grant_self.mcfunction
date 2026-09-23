# Donne un stock utile, puis mémorise le temps de serveur. 0.8.29 : alchimie rendue plus rentable.
execute store result score #now CAPSK_TIME run time query gametime
scoreboard players operation @s CAPSK_ALCH_LAST = #now CAPSK_TIME
tag @s add capskills.alch.kit.claimed
give @s minecraft:potion[minecraft:potion_contents={potion:"minecraft:water"}] 6
give @s minecraft:nether_wart 3
give @s minecraft:gunpowder 1
loot give @s loot capskills:alchemy/daily_basic
execute if entity @s[tag=capskills.alchimiste.kit.r2] run give @s minecraft:potion[minecraft:potion_contents={potion:"minecraft:water"}] 3
execute if entity @s[tag=capskills.alchimiste.kit.r2] run give @s minecraft:nether_wart 2
execute if entity @s[tag=capskills.alchimiste.kit.r2] run give @s minecraft:redstone 1
execute if entity @s[tag=capskills.alchimiste.kit.r2] run give @s minecraft:glowstone_dust 1
execute if entity @s[tag=capskills.alchimiste.kit.r2] run loot give @s loot capskills:alchemy/daily_advanced
execute at @s run playsound minecraft:block.brewing_stand.brew player @s ~ ~ ~ 0.65 1.15 0
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Matériel d’alchimie récupéré.","color":"green"},{"text":" Prochaine récupération : environ 20 h de serveur.","color":"gray"}]
