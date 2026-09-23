tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_moving
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_active
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_returning
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_pause_h_done
scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port] NPC_PATROL_CD 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Escorte Léovic]","color":"gold"},{"text":" : Escorte arrêtée.","color":"white"}]
