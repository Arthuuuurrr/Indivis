tag @e[type=marker,tag=cap_zone_toggle_target] remove cap_zone_toggle_target
execute unless entity @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zones protégées] Aucune zone proche à activer/désactiver.","color":"red"}
execute if entity @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] run tag @e[type=marker,tag=cap_zone_protection,distance=..16,sort=nearest,limit=1] add cap_zone_toggle_target
scoreboard players set @s CAP_ZONE_TMP 0
execute if entity @e[type=marker,tag=cap_zone_toggle_target,tag=cap_zone_active,limit=1] run scoreboard players set @s CAP_ZONE_TMP 1
execute if score @s CAP_ZONE_TMP matches 1 run tag @e[type=marker,tag=cap_zone_toggle_target,limit=1] remove cap_zone_active
execute if score @s CAP_ZONE_TMP matches 1 run tellraw @s {"text":"[Zones protégées] Zone proche désactivée.","color":"yellow"}
execute if score @s CAP_ZONE_TMP matches 0 if entity @e[type=marker,tag=cap_zone_toggle_target,limit=1] run tag @e[type=marker,tag=cap_zone_toggle_target,limit=1] add cap_zone_active
execute if score @s CAP_ZONE_TMP matches 0 if entity @e[type=marker,tag=cap_zone_toggle_target,limit=1] run tellraw @s {"text":"[Zones protégées] Zone proche activée.","color":"green"}
tag @e[type=marker,tag=cap_zone_toggle_target] remove cap_zone_toggle_target
