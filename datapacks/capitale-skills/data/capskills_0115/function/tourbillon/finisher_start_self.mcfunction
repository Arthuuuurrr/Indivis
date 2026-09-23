# Préparation visuelle de la frappe au sol lourde, appelée au tick global 52.
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 7
execute at @s run particle minecraft:crit ~ ~1.25 ~ 0.85 0.40 0.85 0.03 18 force @a[distance=..28]
execute at @s run particle minecraft:cloud ~ ~0.20 ~ 1.15 0.05 1.15 0.01 12 force @a[distance=..28]
execute at @s run playsound minecraft:entity.player.attack.strong player @a[distance=..28] ~ ~ ~ 0.70 0.78 0
title @s actionbar {"text":"Inertie lourde : frappe tellurique !","color":"gold"}
