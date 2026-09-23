# Vérification légère des zones protégées — 1 fois/seconde.
tag @a remove cap_zone_inside_any
tag @a remove cap_zone_inside_couronne
tag @a remove cap_zone_inside_mystere
execute as @e[type=marker,tag=cap_zone_protection,tag=cap_zone_active] run function capitale:zone/protection/check_marker
execute as @a[tag=cap_zone_inside_any,tag=!cap_zone_bypass,gamemode=survival] run function capitale:zone/protection/enter_adventure_self
execute as @a[tag=cap_zone_forced_adventure,tag=!cap_zone_inside_any,gamemode=adventure] run function capitale:zone/protection/exit_survival_self
execute as @a[tag=cap_zone_forced_adventure,tag=cap_zone_bypass,gamemode=adventure] run function capitale:zone/protection/exit_survival_self
execute as @a[tag=cap_zone_forced_adventure,gamemode=creative] run tag @s remove cap_zone_forced_adventure
execute as @a[tag=cap_zone_forced_adventure,gamemode=spectator] run tag @s remove cap_zone_forced_adventure
