execute positioned as @e[tag=npc_colin_ferand,limit=1] if entity @a[scores={QUEST_PROFONDEURS=50},distance=..24] if entity @e[type=marker,tag=wp_colin_profondeurs_x,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_colin_ferand,limit=1] if entity @a[scores={QUEST_PROFONDEURS=50},distance=..24] if entity @e[type=marker,tag=wp_colin_profondeurs_x,limit=1] run scoreboard players set @s NPC_PATROL_STATE 23
execute positioned as @e[tag=npc_colin_ferand,limit=1] unless entity @a[scores={QUEST_PROFONDEURS=50},distance=..24] run function capitale:quest/au_seuil_des_profondeurs/escort_colin/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 40
