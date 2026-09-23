scoreboard players add @s CAPSK_XP 12
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+12 XP de compétence","color":"aqua"},{"text":" — quête quotidienne complète.","color":"gray"}]
execute at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.6 1.28 0
function capskills:reward/xp/check_self
