# @s = cible vulnérable touchée par un projectile d'arbalète.
damage @s 2 minecraft:generic
effect give @s minecraft:glowing 2 0 true
execute at @s run particle minecraft:damage_indicator ~ ~1.0 ~ 0.35 0.35 0.35 0.02 10 force @a[distance=..40]
execute at @s run particle minecraft:electric_spark ~ ~1.1 ~ 0.45 0.35 0.45 0.04 18 force @a[distance=..40]
execute at @s run playsound minecraft:entity.arrow.hit_player player @a[distance=..36] ~ ~ ~ 0.55 1.35 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Marque de vulnérabilité consommée","color":"gold"}
tag @s remove capskills.arbalete.vulnerable
scoreboard players set @s CAPSK_ARBA_VULN_T 0
