# Tick phasé : appelé toutes les 5 ticks, uniquement s’il existe au moins un garde actif.
# On retire 5 ticks au timer pour conserver le retour après environ 30 secondes.
scoreboard players remove @e[tag=cap_static_guard_active,scores={CAP_STATIC_GUARD_RESET=1..}] CAP_STATIC_GUARD_RESET 5
execute as @e[tag=cap_static_guard_active,scores={CAP_STATIC_GUARD_RESET=..0}] at @s run function capitale:npc/static_guards/defend_reset/reset_self
