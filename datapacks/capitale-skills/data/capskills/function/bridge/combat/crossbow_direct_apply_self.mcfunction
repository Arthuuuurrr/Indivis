# CapSkills 0.9.19 — application directe des effets d'arbalète sur cible raycast.
# @s = arbalétrier ; cible = entité taggée capskills.crossbow_direct_target.
scoreboard players set @s CAPSK_CROSS_DIRECT_HIT 1
# 0.9.22 debug désactivé : tellraw @s {"text":"[CapSkills DBG] fallback direct : hitbox cible acquise.","color":"dark_green"}

# Anti-armure : contrôle léger.
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 2 0 true
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 2 0 true

# Stabilisation : preuve visuelle principale.
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:instant_effect ~ ~1.2 ~ 0.28 0.20 0.28 0.02 12 force @a[distance=..32]
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:instant_effect ~ ~1.2 ~ 0.28 0.20 0.28 0.02 12 force @a[distance=..32]

# Perce-armure : feedback visuel en attendant dégâts/armure définitifs.
execute if entity @s[tag=capskills.arbalete.percearmure.r1] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.22 0.22 0.22 0.02 4 force @a[distance=..32]
execute if entity @s[tag=capskills.skill.trait_arbalete_percearmure_1] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.22 0.22 0.22 0.02 4 force @a[distance=..32]

# Carreau d'arrêt.
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 3 1 true
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 3 1 true
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 3 0 true
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 3 0 true

# Salve préparée : effet fort. Les tags pending/just_shot couvrent le fallback qui consomme l'état.
execute if entity @s[tag=capskills.arbalete.salve.ready] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
execute if entity @s[tag=capskills.arbalete.salve.just_shot] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
execute if entity @s[tag=capskills.arbalete.salve.ready] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.just_shot] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:slowness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.ready] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.just_shot] run effect give @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] minecraft:weakness 4 1 true
execute if entity @s[tag=capskills.arbalete.salve.ready] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.1 ~ 0.55 0.35 0.55 0.05 36 force @a[distance=..40]
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.1 ~ 0.55 0.35 0.55 0.05 36 force @a[distance=..40]
execute if entity @s[tag=capskills.arbalete.salve.just_shot] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.1 ~ 0.55 0.35 0.55 0.05 36 force @a[distance=..40]
execute if entity @s[tag=capskills.arbalete.salve.ready] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run playsound minecraft:block.anvil.land player @a[distance=..40] ~ ~ ~ 0.45 1.25 0
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run playsound minecraft:block.anvil.land player @a[distance=..40] ~ ~ ~ 0.45 1.25 0
execute if entity @s[tag=capskills.arbalete.salve.just_shot] at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run playsound minecraft:block.anvil.land player @a[distance=..40] ~ ~ ~ 0.45 1.25 0

execute at @e[tag=capskills.crossbow_direct_target,limit=1,sort=nearest] run function capskills:visual/activation/arbalete_impact_self
tag @e[tag=capskills.crossbow_direct_target] remove capskills.crossbow_direct_target
