tag @e[type=marker,tag=cap_nomob_new] remove cap_nomob_new
summon minecraft:marker ~ ~ ~ {Tags:["cap_nomob_zone","cap_nomob_active","cap_nomob_new"]}
scoreboard players set @e[type=marker,tag=cap_nomob_new,sort=nearest,limit=1] CAP_NOMOB_RADIUS 800
tag @e[type=marker,tag=cap_nomob_new,sort=nearest,limit=1] remove cap_nomob_new
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Zone anti-spawn]","color":"dark_green","bold":true},{"text":" Zone créée ici — rayon 800 blocs. Hostiles vanilla nettoyés : zombie, squelette, creeper, araignée, husk, stray, noyé, sorcière, phantom.","color":"white"}]
tellraw @s [{"text":"Protection : ","color":"gray"},{"text":"les mobs taggés cap_keep_mob, cap_quest_mob, cap_boss ou cap_no_clean sont ignorés.","color":"white"}]
