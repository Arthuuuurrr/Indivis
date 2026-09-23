scoreboard players add @s CAPSK_XP 5
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+5 XP de compétence","color":"aqua"},{"text":" — tâche quotidienne courte.","color":"gray"}]
execute at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.45 1.20 0
function capskills:reward/xp/check_self
