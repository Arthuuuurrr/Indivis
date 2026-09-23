execute unless entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] run tellraw @a[distance=..12] {"text":"[Mira Q01] Guide absent. Utilisez /function capitale:npc/mira/path/q01/admin/menu_self","color":"red"}
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_CD 0
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 0
scoreboard players add @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STUCK 0
tag @e[type=armor_stand,tag=guide_mira_q01,limit=1] add patrol_active
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_CD 20
scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 0
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_mira_q01,limit=1] NPC_PATROL_MODE 1
# Priorité : guide sur Mira actuelle. Fallback : HOME.
execute if entity @e[tag=npc_mira_q01,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[tag=npc_mira_q01,sort=nearest,limit=1]
execute unless entity @e[tag=npc_mira_q01,limit=1] if entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[type=marker,tag=wp_mira_q01_home,limit=1]
execute unless entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tellraw @a[distance=..12] {"text":"[Mira Q01] Marker HOME absent.","color":"red"}
execute unless entity @e[tag=npc_mira_q01,limit=1] run tellraw @a[distance=..12] {"text":"[Mira Q01] Aucun PNJ tagué npc_mira_q01. Le guide partira de HOME, mais Mira ne suivra pas correctement.","color":"yellow"}
