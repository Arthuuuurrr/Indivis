scoreboard players add @s CAPSK_XP 25
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+25 XP de compétence","color":"light_purple","bold":true},{"text":" — première réussite de cette journalière.","color":"gray"}]
execute at @s run playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.65 1.65 0
function capskills:reward/xp/check_self
