# CapSkills 0.9.15 — appelé une seule fois par le jar quand un projectile d'arbalète reçoit son boost de vélocité.
# @s = arbalétrier. capskills.arbalete.salve.fired est présent uniquement pour le carreau de Salve.
execute if entity @s[tag=capskills.arbalete.salve.fired] at @s run particle minecraft:end_rod ~ ~0.55 ~ 0.75 0.12 0.75 0.02 14 force @a[distance=..36]
execute if entity @s[tag=capskills.arbalete.salve.fired] at @s run particle minecraft:electric_spark ~ ~0.60 ~ 0.60 0.15 0.60 0.03 18 force @a[distance=..36]
execute if entity @s[tag=capskills.arbalete.salve.fired] at @s run playsound minecraft:item.crossbow.shoot player @a[distance=..36] ~ ~ ~ 0.60 0.55 0
execute unless entity @s[tag=capskills.arbalete.salve.fired] at @s run particle minecraft:crit ~ ~0.55 ~ 0.45 0.10 0.45 0.02 6 force @a[distance=..28]
