# Démarre un retour progressif HOME via les points r12 -> r01 -> HOME.
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_CD 0
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 0
tag @e[type=armor_stand,tag=guide_mira_q01,limit=1] add patrol_active
execute if entity @e[tag=npc_mira_q01,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[tag=npc_mira_q01,sort=nearest,limit=1]
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 2
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STATE 12
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_CD 1
