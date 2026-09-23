tag @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_14] remove patrol_active
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_14] NPC_PATROL_CD 0
tellraw @s {"text": "[Gardes des Profondeurs] Patrouille 14 arrêtée.", "color": "yellow"}
