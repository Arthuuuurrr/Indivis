# Téléporte le garde actif vers son ancre personnelle.
# Utilise un ID scoreboard commun entre le PNJ et son marker.
tag @s add cap_static_guard_current_reset
scoreboard players operation #current CAP_STATIC_GUARD_ID = @s CAP_STATIC_GUARD_ID
execute as @e[type=minecraft:marker,tag=cap_static_guard_anchor] if score @s CAP_STATIC_GUARD_ID = #current CAP_STATIC_GUARD_ID at @s run tp @e[tag=cap_static_guard_current_reset,limit=1] ~ ~ ~
tag @e[tag=cap_static_guard_current_reset,limit=1] remove cap_static_guard_current_reset
