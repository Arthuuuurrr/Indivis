tag @e[type=marker,tag=cap_zone_new] remove cap_zone_new
summon minecraft:marker ~ ~ ~ {Tags:["cap_zone_protection","cap_zone_active","cap_zone_new","cap_zone_mystere"]}
scoreboard players set @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] CAP_ZONE_RADIUS 600
scoreboard players set @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] CAP_ZONE_RADIUS2 360000
tag @e[type=marker,tag=cap_zone_new,sort=nearest,limit=1] remove cap_zone_new
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Périmètres anciens]","color":"dark_purple","bold":true},{"text":" Périmètre d’aventure créé ici — rayon 600 blocs.","color":"white"}]
