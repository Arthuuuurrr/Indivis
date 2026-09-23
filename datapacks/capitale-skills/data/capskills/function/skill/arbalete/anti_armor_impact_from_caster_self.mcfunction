# CapSkills 0.9.15 — impact arbalète. @s = tireur ; cible = capskills.crossbow_target.
execute if entity @e[tag=capskills.crossbow_target,limit=1,sort=nearest] at @s run function capskills:visual/activation/arbalete_impact_self

# Socle anti-armure : contrôle court, pas de dégâts purs massifs.
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:slowness 2 0 true

# Stabilisation : lecture de cible, utile groupe/PvP sans augmenter la cadence.
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:instant_effect ~ ~1.2 ~ 0.28 0.20 0.28 0.02 8 force @a[distance=..32]

# Perce-armure : petit dégât fixe, volontairement mesuré avec enchantements.
execute if entity @s[tag=capskills.arbalete.percearmure.r1] run damage @e[tag=capskills.crossbow_target,limit=1,sort=nearest] 1 minecraft:generic by @s
execute if entity @s[tag=capskills.arbalete.percearmure.r1] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.22 0.22 0.22 0.02 3 force @a[distance=..32]

# Carreau d'arrêt : anti-charge / contrôle.
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:slowness 3 1 true
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:weakness 3 0 true

# Salve préparée : l'état est désormais porté par le projectile via capskills.arbalete.salve.impact.
# Cela permet de consommer la charge au tir tout en appliquant les effets à l'impact.
execute if entity @s[tag=capskills.arbalete.salve.impact] run damage @e[tag=capskills.crossbow_target,limit=1,sort=nearest] 2 minecraft:generic by @s
execute if entity @s[tag=capskills.arbalete.salve.impact] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:slowness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.impact] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:weakness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.impact] run effect give @e[tag=capskills.crossbow_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
execute if entity @s[tag=capskills.arbalete.salve.impact] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.1 ~ 0.55 0.35 0.55 0.05 36 force @a[distance=..40]
execute if entity @s[tag=capskills.arbalete.salve.impact] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:electric_spark ~ ~1.1 ~ 0.50 0.30 0.50 0.04 28 force @a[distance=..40]
execute if entity @s[tag=capskills.arbalete.salve.impact] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run playsound minecraft:block.anvil.land player @a[distance=..40] ~ ~ ~ 0.50 1.25 0
execute if entity @s[tag=capskills.arbalete.salve.impact] at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run playsound minecraft:item.crossbow.shoot player @a[distance=..40] ~ ~ ~ 0.55 0.70 0
