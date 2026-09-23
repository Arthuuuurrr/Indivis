execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20..40},distance=..24] if entity @e[type=marker,tag=wp_aurele_coeur_d,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20..40},distance=..24] if entity @e[type=marker,tag=wp_aurele_coeur_d,limit=1] run scoreboard players set @s NPC_PATROL_STATE 3
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] unless entity @a[scores={QUEST_GARDECOEUR=20..40},distance=..24] run function capitale:quest/sous_le_regard_du_coeur/escort/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 40
