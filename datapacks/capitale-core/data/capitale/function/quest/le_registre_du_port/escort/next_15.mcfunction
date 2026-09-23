
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=50},distance=..24] as @a[scores={QUEST_GARDEPORT=50},distance=..24,sort=nearest,limit=1] run function capitale:quest/le_registre_du_port/leovic/arrive_ascenseur_self
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=60},distance=..24] run tag @s remove escort_active
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] unless entity @a[scores={QUEST_GARDEPORT=50},distance=..24] unless entity @a[scores={QUEST_GARDEPORT=60},distance=..24] run function capitale:quest/le_registre_du_port/escort/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 80
