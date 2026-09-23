execute unless entity @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] run tellraw @s [{"text":"[Gardes des Profondeurs]","color":"#FF8C00"},{"text":" : Guide absent pour la route 02. Créez-le d’abord.","color":"white"}]
scoreboard players add @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_CD 0
scoreboard players add @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_STUCK 0
tag @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] add patrol_active
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_CD 20
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_MODE 1
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] NPC_PATROL_DIR 0
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_02_a,limit=1] run tp @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_02,limit=1] @e[type=marker,tag=wp_gardeprofondeurs_patrol_02_a,limit=1]
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_02_a,limit=1] run tellraw @s [{"text":"[Gardes des Profondeurs]","color":"#FF8C00"},{"text":" : Attention : point A absent pour la route 02.","color":"white"}]
tellraw @s {"text": "[Gardes des Profondeurs] Patrouille 02 démarrée en aller-retour.", "color": "gray"}
