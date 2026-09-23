puffish_skills category unlock @s capskills:doctrine
scoreboard players remove @s CAPSK_XP 100
scoreboard players add @s CAPSK_LEVEL 1
puffish_skills points add @s capskills:doctrine 1
execute at @s run playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.9 1.45 0
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+1 point de compétence","color":"aqua","bold":true},{"text":" obtenu par progression de quête.","color":"gray"}]
execute if score @s CAPSK_XP matches 100.. run function capskills:reward/xp/check_self
