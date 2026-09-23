execute positioned as @e[tag=npc_roch_vallet,limit=1] if entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] if entity @e[type=marker,tag=wp_roch_profondeurs_h,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_roch_vallet,limit=1] if entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] if entity @e[type=marker,tag=wp_roch_profondeurs_h,limit=1] run scoreboard players set @s NPC_PATROL_STATE 7
execute positioned as @e[tag=npc_roch_vallet,limit=1] unless entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] run function capitale:quest/au_seuil_des_profondeurs/escort_roch/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 40
