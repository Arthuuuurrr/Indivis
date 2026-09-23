# Retour au poste ancré après poursuite/défense.
execute if score @s CAP_STATIC_GUARD_ID matches 1.. run function capitale:npc/static_guards/defend_reset/return_to_anchor_self
scoreboard players set @s CAP_STATIC_GUARD_RESET 0
tag @s remove cap_static_guard_active
