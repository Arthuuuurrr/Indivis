scoreboard players set @s CAP_GUIDE_LOCK 1
scoreboard players set @s CAP_GUIDE_ID 2
scoreboard players set @s CAP_GUIDE_MISS_T 0
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
execute unless entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tellraw @s [{"text":"[Escorte Aurèle]","color":"red"},{"text":" : Guide d’escorte absent. Aurèle ne pourra pas repartir tant que la route n’est pas configurée.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tellraw @s [{"text":"[Escorte Aurèle]","color":"red"},{"text":" : Point A absent pour l’escorte.","color":"white"}]
execute unless entity @e[type=marker,tag=wp_aurele_coeur_v,limit=1] run function capitale:dialogue/sound/parole_quete_self
execute unless entity @e[type=marker,tag=wp_aurele_coeur_v,limit=1] run tellraw @s [{"text":"[Escorte Aurèle]","color":"red"},{"text":" : Point V absent pour l’arrivée vers les Profondeurs.","color":"white"}]
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] remove escort_returning
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] remove escort_pause_d_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] remove escort_pause_g_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] remove escort_pause_n_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] remove escort_pause_s_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] add escort_active
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_STATE 8
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_CD 5
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
execute if entity @e[tag=npc_aurele_veyrane,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[tag=npc_aurele_veyrane,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_RETURN_TIMER 0
