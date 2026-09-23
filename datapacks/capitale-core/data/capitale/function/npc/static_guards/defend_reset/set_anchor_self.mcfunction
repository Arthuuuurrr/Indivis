# Crée ou met à jour l'ancre de retour du garde statique à sa position actuelle.
# À appeler en contexte PNJ, idéalement avant combat : EasyNPC executeAsUser=false.
function capitale:npc/static_guards/defend_reset/assign_id_self
tag @s add cap_static_guard_resettable
scoreboard players operation #current CAP_STATIC_GUARD_ID = @s CAP_STATIC_GUARD_ID
execute as @e[type=minecraft:marker,tag=cap_static_guard_anchor] if score @s CAP_STATIC_GUARD_ID = #current CAP_STATIC_GUARD_ID run kill @s
summon minecraft:marker ~ ~ ~ {Tags:["cap_static_guard_anchor","cap_static_guard_anchor_new"]}
scoreboard players operation @e[type=minecraft:marker,tag=cap_static_guard_anchor_new,sort=nearest,limit=1,distance=..1.5] CAP_STATIC_GUARD_ID = @s CAP_STATIC_GUARD_ID
tag @e[type=minecraft:marker,tag=cap_static_guard_anchor_new,sort=nearest,limit=1,distance=..1.5] remove cap_static_guard_anchor_new
