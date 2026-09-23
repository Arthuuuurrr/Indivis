# Retour HOME contrôlé : ne téléporte plus le guide directement trop loin.
# Si Mira est taguée, le guide repart de sa position actuelle puis revient par la route inverse.
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,limit=1] run function capitale:npc/mira/path/q01/return_route_start
# Fallback sans Mira taguée : retour instantané du guide pour éviter un guide bloqué.
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] unless entity @e[tag=npc_mira_q01,limit=1] if entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[type=marker,tag=wp_mira_q01_home,limit=1]
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] unless entity @e[tag=npc_mira_q01,limit=1] run tag @e[type=armor_stand,tag=guide_mira_q01,limit=1] remove patrol_active
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] unless entity @e[tag=npc_mira_q01,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STATE 0
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] unless entity @e[tag=npc_mira_q01,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 0
execute unless entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] run tellraw @a[distance=..12] {"text":"[Mira Q01] Guide absent : impossible de lancer le retour HOME.","color":"red"}
