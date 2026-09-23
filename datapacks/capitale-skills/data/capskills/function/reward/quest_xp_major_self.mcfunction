scoreboard players add @s CAPSK_XP 100
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"+100 XP de compétence","color":"aqua"},{"text":" — quête majeure.","color":"gray"}]
execute at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.65 1.25 0
function capskills:reward/xp/check_self
