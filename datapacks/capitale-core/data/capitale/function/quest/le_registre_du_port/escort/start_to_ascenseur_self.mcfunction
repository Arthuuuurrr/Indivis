tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_moving

execute unless entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Guide d’escorte absent. La quête reste active, mais Léovic ne pourra pas repartir vers l’ascenseur tant que la route n’est pas configurée.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point M absent : Léovic ne sait pas d’où repartir après le magistrat.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point N absent pour le trajet vers l’ascenseur.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_o,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_o,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point O absent pour le trajet vers l’ascenseur.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] run tellraw @s [{"text":"[Escorte Léovic]","color":"red"},{"text":" : Point P absent : l’arrivée devant l’ascenseur n’est pas définie.","color":"white"}]
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] remove escort_returning
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] add escort_active
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 12
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run tp @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] @e[type=marker,tag=wp_leovic_registre_port_m,limit=1]
