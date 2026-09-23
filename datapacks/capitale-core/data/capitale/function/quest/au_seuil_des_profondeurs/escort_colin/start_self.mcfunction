scoreboard players set @s CAP_GUIDE_LOCK 1
scoreboard players set @s CAP_GUIDE_ID 4
scoreboard players set @s CAP_GUIDE_MISS_T 0
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_moving
execute unless entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Guide d’escorte absent. La quête reste active, mais le PNJ ne pourra pas avancer tant que la route n’est pas configurée.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_a,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_a,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point A absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_b,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_b,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point B absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_c,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_c,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point C absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_d,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_d,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point D absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_e,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_e,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point E absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_f,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_f,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point F absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_g,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_g,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point G absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_h,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_h,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point H absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_i,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_i,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point I absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_j,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_j,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point J absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_k,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_k,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point K absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_l,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_l,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point L absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_m,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_m,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point M absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_n,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_n,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point N absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_o,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_o,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point O absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_p,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_p,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point P absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_q,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_q,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point Q absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_r,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_r,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point R absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_s,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_s,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point S absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_t,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_t,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point T absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_u,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_u,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point U absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_v,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_v,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point V absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_w,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_w,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point W absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_x,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_x,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point X absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_y,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_y,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point Y absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_z,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_colin_profondeurs_z,limit=1] run tellraw @s [{"text":"[Escorte Colin]","color":"red"},{"text":" : Point Z absent pour l’escorte.","color":"white"}]
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] remove escort_returning
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] remove escort_pause_d_done
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] remove escort_pause_h_done
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] add escort_active
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_STATE 0
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] if entity @e[type=marker,tag=wp_colin_profondeurs_a,limit=1] run tp @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] @e[type=marker,tag=wp_colin_profondeurs_a,limit=1]
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_RETURN_TIMER 0
