# @s = cible du carreau de rupture.
damage @s 1 minecraft:generic
effect give @s minecraft:weakness 7 1 true
effect give @s minecraft:glowing 7 0 true
execute at @s run particle minecraft:damage_indicator ~ ~1.0 ~ 0.45 0.35 0.45 0.025 10 force @a[distance=..40]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.42 0.30 0.42 0.04 16 force @a[distance=..40]
execute at @s run playsound minecraft:entity.arrow.hit_player player @a[distance=..36] ~ ~ ~ 0.55 0.80 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Rupture d’arbalète reçue","color":"gold"}
