# Capitale 1.5.6 RC3 — garde statique blessé, contexte PNJ attendu.
# À appeler depuis EasyNPC On Hurt avec executeAsUser=false.
# La punition du joueur reste dans on_hurt_self avec executeAsUser=true.
# Cette fonction active seulement le retour au poste après défense/poursuite.

tag @s add cap_static_guard_active
tag @s add cap_static_guard_resettable
execute unless score @s CAP_STATIC_GUARD_ID matches 1.. run function capitale:npc/static_guards/defend_reset/set_anchor_self
scoreboard players operation #current CAP_STATIC_GUARD_ID = @s CAP_STATIC_GUARD_ID
scoreboard players set #found CAP_STATIC_GUARD_ID 0
execute as @e[type=minecraft:marker,tag=cap_static_guard_anchor] if score @s CAP_STATIC_GUARD_ID = #current CAP_STATIC_GUARD_ID run scoreboard players set #found CAP_STATIC_GUARD_ID 1
execute unless score #found CAP_STATIC_GUARD_ID matches 1 run function capitale:npc/static_guards/defend_reset/set_anchor_self
scoreboard players set @s CAP_STATIC_GUARD_RESET 600
