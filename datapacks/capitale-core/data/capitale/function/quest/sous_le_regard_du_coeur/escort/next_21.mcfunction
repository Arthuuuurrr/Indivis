
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=40},distance=..24] as @a[scores={QUEST_GARDECOEUR=40},distance=..24,sort=nearest,limit=1] run function capitale:quest/sous_le_regard_du_coeur/aurele/arrive_profondeurs_self
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=50},distance=..24] run tag @s remove escort_active
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] unless entity @a[scores={QUEST_GARDECOEUR=40},distance=..24] unless entity @a[scores={QUEST_GARDECOEUR=50},distance=..24] run function capitale:quest/sous_le_regard_du_coeur/escort/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 80
