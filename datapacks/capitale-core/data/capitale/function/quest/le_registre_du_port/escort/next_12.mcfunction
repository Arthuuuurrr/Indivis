
# Phase 1 — arrivée devant le magistrat
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run tellraw @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Nous y sommes. Le magistrat officie ici ; présentez-vous à lui, puis revenez me voir.","color":"white"}]
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run playsound minecraft:item.book.page_turn master @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] ~ ~ ~ 288.00 0.95
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run tellraw @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] {"text":"[Quête] Le registre du Port — Présentez-vous au magistrat du Port.","color":"gold"}
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run title @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] times 5 50 15
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run title @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run title @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] subtitle {"text":"Présentez-vous au magistrat du Port.","color":"white"}
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] as @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.55 1.35
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=20},distance=..24] run scoreboard players set @a[scores={QUEST_GARDEPORT=20},distance=..24,sort=nearest,limit=1] QUEST_GARDEPORT 30
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=30},distance=..24] run scoreboard players set @a[scores={QUEST_GARDEPORT=30},distance=..24,sort=nearest,limit=1] CAP_QUETEACTIVE 0
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=30},distance=..24,sort=nearest,limit=1] run tag @s remove escort_active

# Phase 2 — reprise après l’enregistrement vers l’ascenseur
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=50},distance=..24] if entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] if entity @a[scores={QUEST_GARDEPORT=50},distance=..24] if entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run scoreboard players set @s NPC_PATROL_STATE 13

# Attente si le joueur est trop loin dans l’une ou l’autre phase
execute positioned as @e[tag=npc_leovic_registre_port,limit=1] unless entity @a[scores={QUEST_GARDEPORT=20},distance=..24] unless entity @a[scores={QUEST_GARDEPORT=50},distance=..24] run function capitale:quest/le_registre_du_port/escort/wait_if_far_self
scoreboard players set @s NPC_PATROL_CD 80
