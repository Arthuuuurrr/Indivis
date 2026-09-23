tag @e[type=marker,tag=cap_nomob_toggle_target] remove cap_nomob_toggle_target
execute unless entity @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] run tellraw @s {"text":"[Zone anti-spawn] Aucune zone proche à activer/désactiver.","color":"red"}
execute if entity @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] run tag @e[type=marker,tag=cap_nomob_zone,distance=..16,sort=nearest,limit=1] add cap_nomob_toggle_target
scoreboard players set @s CAP_NOMOB_TMP 0
execute if entity @e[type=marker,tag=cap_nomob_toggle_target,tag=cap_nomob_active,limit=1] run scoreboard players set @s CAP_NOMOB_TMP 1
execute if score @s CAP_NOMOB_TMP matches 1 run tag @e[type=marker,tag=cap_nomob_toggle_target,limit=1] remove cap_nomob_active
execute if score @s CAP_NOMOB_TMP matches 1 run tellraw @s {"text":"[Zone anti-spawn] Zone proche désactivée.","color":"yellow"}
execute if score @s CAP_NOMOB_TMP matches 0 if entity @e[type=marker,tag=cap_nomob_toggle_target,limit=1] run tag @e[type=marker,tag=cap_nomob_toggle_target,limit=1] add cap_nomob_active
execute if score @s CAP_NOMOB_TMP matches 0 if entity @e[type=marker,tag=cap_nomob_toggle_target,limit=1] run tellraw @s {"text":"[Zone anti-spawn] Zone proche activée.","color":"green"}
tag @e[type=marker,tag=cap_nomob_toggle_target] remove cap_nomob_toggle_target
