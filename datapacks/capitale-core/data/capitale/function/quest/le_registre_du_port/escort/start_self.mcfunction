scoreboard players set @s CAP_GUIDE_LOCK 1
scoreboard players set @s CAP_GUIDE_ID 1
scoreboard players set @s CAP_GUIDE_MISS_T 0
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_moving
execute unless entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Guide d’escorte absent. La quête reste active, mais Léovic ne pourra pas avancer tant que la route n’est pas configurée.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_a,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_a,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point A absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_b,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_b,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point B absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_c,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_c,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point C absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_d,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_d,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point D absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_e,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_e,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point E absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_f,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_f,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point F absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_g,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_g,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point G absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point H absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_i,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_i,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point I absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_j,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_j,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point J absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_k,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_k,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point K absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_l,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_l,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point L absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point M absent pour l’escorte.","color":"white"}]
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] remove escort_returning
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] remove escort_pause_d_done
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] remove escort_pause_h_done
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] add escort_active
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 0
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_a,limit=1] run tp @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] @e[type=marker,tag=wp_leovic_registre_port_a,limit=1]
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_RETURN_TIMER 0
