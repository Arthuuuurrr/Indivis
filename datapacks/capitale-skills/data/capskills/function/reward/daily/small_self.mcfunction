scoreboard players add @s CAPSK_XP 8
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+8 XP de compétence","color":"aqua"},{"text":" — quête quotidienne simple.","color":"gray"}]
execute at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.5 1.22 0
function capskills:reward/xp/check_self
