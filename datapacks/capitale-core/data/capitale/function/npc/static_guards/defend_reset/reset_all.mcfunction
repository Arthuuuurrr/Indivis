# Admin : ramène tous les gardes statiques actifs à leur ancre.
execute as @e[tag=cap_static_guard_active] at @s run function capitale:npc/static_guards/defend_reset/reset_self
tellraw @s {"text":"[Capitale] Reset des gardes statiques actifs demandé.","color":"green"}
