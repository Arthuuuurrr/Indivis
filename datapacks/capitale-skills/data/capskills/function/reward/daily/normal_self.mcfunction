scoreboard players add @s CAPSK_XP 10
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+10 XP de compétence","color":"aqua"},{"text":" — quête quotidienne normale.","color":"gray"}]
execute at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.55 1.25 0
function capskills:reward/xp/check_self
