# CapSkills BETA 0.9.31 — visuel persistant du Root du Rempart.
# @s = cible actuellement rootée.
particle minecraft:electric_spark ~ ~1.05 ~ 0.34 0.58 0.34 0.025 8 force @a[distance=..32]
particle minecraft:end_rod ~ ~1.15 ~ 0.22 0.45 0.22 0.010 4 force @a[distance=..32]
particle minecraft:enchanted_hit ~ ~0.95 ~ 0.28 0.45 0.28 0.030 5 force @a[distance=..32]
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Root du Rempart — immobilisé","color":"blue"}
scoreboard players remove @s CAPSK_REMP_ROOT_VIS 1
execute if score @s CAPSK_REMP_ROOT_VIS matches ..0 run scoreboard players set @s CAPSK_REMP_ROOT_VIS 0
execute if score @s CAPSK_REMP_ROOT_VIS matches ..0 run tag @s remove capskills.rempart.rooted_visual
