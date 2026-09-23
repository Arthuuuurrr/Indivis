execute unless entity @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zones protégées] Aucune zone à supprimer dans un rayon de 16 blocs.","color":"red"}
execute if entity @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zones protégées] Zone proche supprimée.","color":"gold"}
execute if entity @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] run kill @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1]
