execute positioned as @e[tag=npc_roch_vallet,limit=1] if entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] as @a[scores={QUEST_PROFONDEURS=20},distance=..24,sort=nearest,limit=1] run function capitale:quest/au_seuil_des_profondeurs/roch/arrive_colin_self
execute positioned as @e[tag=npc_roch_vallet,limit=1] if entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] run tag @s remove escort_active
execute positioned as @e[tag=npc_roch_vallet,limit=1] unless entity @a[scores={QUEST_PROFONDEURS=20},distance=..24] run function capitale:quest/au_seuil_des_profondeurs/escort_roch/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 80
