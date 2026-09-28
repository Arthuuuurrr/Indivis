# RC9AV PERF1 : même tag que le scan de pré-détection, sans changer portée/cadence.
execute if block ~ ~ ~ #capitale:adventure_bare_hand_fire run function capitale:adventure/fire_extinguish/bare_hand_extinguish_here
execute if block ~ ~ ~ #capitale:adventure_bare_hand_fire run return 1
execute unless block ~ ~ ~ #minecraft:replaceable run return 0
scoreboard players add @s CAP_FIRE_RAY 1
execute if score @s CAP_FIRE_RAY matches ..19 positioned ^ ^ ^0.25 run function capitale:adventure/fire_extinguish/bare_hand_hit_step
