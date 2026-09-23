tag @e[type=marker,tag=cap_zone_new] remove cap_zone_new
summon minecraft:marker ~ ~ ~ {Tags:["cap_zone_protection","cap_zone_active","cap_zone_new","cap_zone_capitale"]}
scoreboard players set @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] CAP_ZONE_RADIUS 800
scoreboard players set @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] CAP_ZONE_RADIUS2 640000
tag @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] remove cap_zone_new
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Zones protégées]","color":"gold","bold":true},{"text":" Zone créée ici — rayon 800 blocs.","color":"white"}]
