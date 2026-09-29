# 1.2.45 — neutralise uniquement les skeleton jockeys créés comme passagers des serpents.
execute as @e[type=cubeanimals:rattlesnake] at @s on passengers if entity @s[type=minecraft:skeleton] run function capitale_creatures:runtime/remove_snake_skeleton_jockey
execute as @e[type=capitale_entities:snake] at @s on passengers if entity @s[type=minecraft:skeleton] run function capitale_creatures:runtime/remove_snake_skeleton_jockey
