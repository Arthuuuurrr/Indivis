execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] if entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] if entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run scoreboard players set @s NPC_PATROL_STATE 12
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] unless entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run function capitale:quest/le_registre_du_port/escort/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 40
