# Répartition des patrouilles sur 4 phases : Cœur / Port / Profondeurs / repos
scoreboard players add #cap_patrol NPC_PATROL_PHASE 1
execute if score #cap_patrol NPC_PATROL_PHASE matches 4.. run scoreboard players set #cap_patrol NPC_PATROL_PHASE 0
execute if score #cap_patrol NPC_PATROL_PHASE matches 0 run function capitale:npc/gardes/tick_gardecoeur
execute if score #cap_patrol NPC_PATROL_PHASE matches 1 run function capitale:npc/gardes/tick_gardeport
execute if score #cap_patrol NPC_PATROL_PHASE matches 2 run function capitale:npc/gardes/tick_gardeprofondeurs
