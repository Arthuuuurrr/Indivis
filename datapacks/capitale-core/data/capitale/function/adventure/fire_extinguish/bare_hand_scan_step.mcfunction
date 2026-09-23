# Feu trouvé : place un détecteur invisible dans la portée d'attaque entité.
execute if block ~ ~ ~ minecraft:fire run function capitale:adventure/fire_extinguish/bare_hand_spawn_detector
execute if block ~ ~ ~ minecraft:fire run return 1
execute if block ~ ~ ~ minecraft:soul_fire run function capitale:adventure/fire_extinguish/bare_hand_spawn_detector
execute if block ~ ~ ~ minecraft:soul_fire run return 1
# Arrêt sur un bloc solide/non remplaçable : pas d'extinction à travers les murs.
execute unless block ~ ~ ~ #minecraft:replaceable run return 0
# Raycast jusqu'à 5 blocs, pas de 0,25 bloc.
scoreboard players add @s CAP_FIRE_RAY 1
execute if score @s CAP_FIRE_RAY matches ..19 positioned ^ ^ ^0.25 run function capitale:adventure/fire_extinguish/bare_hand_scan_step
