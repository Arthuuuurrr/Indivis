# Near Distance à placer sur Mira, exécuté comme le joueur.
# Rôle : relancer/amorcer le guide quand un joueur ayant Q01 en cours approche de Mira.
scoreboard players add @s QUEST_SIDE_BA_Q01 0
# Si le joueur a accepté Q01 et que la route n'est pas active, on la lance depuis Mira.
execute if score @s QUEST_SIDE_BA_Q01 matches 20 if entity @e[tag=npc_mira_q01,distance=..10,limit=1] unless entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,limit=1] run function capitale:npc/mira/path/q01/start_from_mira_self
# Si la route est active mais encore à l'état initial et que le guide n'est pas proche de Mira, on recale le guide sur Mira pour débloquer le départ.
execute if score @s QUEST_SIDE_BA_Q01 matches 20 as @e[tag=npc_mira_q01,distance=..10,limit=1,sort=nearest] at @s if entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,scores={NPC_PATROL_STATE=0},limit=1] unless entity @e[type=armor_stand,tag=guide_mira_q01,distance=..3,limit=1] run function capitale:npc/mira/path/q01/snap_guide_to_mira
