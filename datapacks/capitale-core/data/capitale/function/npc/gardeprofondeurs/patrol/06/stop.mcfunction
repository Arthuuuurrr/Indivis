tag @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_06] remove patrol_active
scoreboard players set @e[type=armor_stand,tag=guide_gardeprofondeurs_patrol_06] NPC_PATROL_CD 0
tellraw @s {"text": "[Gardes des Profondeurs] Patrouille 06 arrêtée.", "color": "yellow"}
