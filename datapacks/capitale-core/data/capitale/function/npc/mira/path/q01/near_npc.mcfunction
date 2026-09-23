# Near Distance alternative si EasyNPC exécute le near comme le PNJ Mira.
# Cette fonction tague le PNJ et recale le guide s'il est actif mais trop loin.
tag @s add npc_mira_q01
execute if entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,scores={NPC_PATROL_STATE=0},limit=1] at @s unless entity @e[type=armor_stand,tag=guide_mira_q01,distance=..3,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @s
