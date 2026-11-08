# PQ03-R1 — Contexte exact de chaque joueur ; proximité bornée à trois blocs
execute if entity @s[type=minecraft:player] run indivispq03 near
execute unless entity @s[type=minecraft:player] at @s as @a[distance=..3] at @s run indivispq03 near
