tag @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_15] remove patrol_active
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_15] NPC_PATROL_CD 0
tellraw @s {"text": "[Gardes des Profondeurs] Patrouille 15 arrêtée.", "color": "yellow"}
