execute unless entity @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zone anti-spawn] Aucune zone à supprimer dans un rayon de 16 blocs.","color":"red"}
execute if entity @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zone anti-spawn] Zone proche supprimée.","color":"dark_green"}
execute if entity @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] run kill @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1]
