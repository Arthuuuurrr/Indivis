# CapSkills 0.9.22 — fallback datapack de vélocité pour les carreaux/flèches d'arbalète.
# @s = projectile taggé capskills.crossbow_projectile.
# Objectif : rendre la trajectoire plus tendue même si le hook Java de vélocité ne passe pas.

# Salve préparée : boost fort prioritaire.
execute if entity @s[tag=capskills.crossbow.salve.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_85_self

# Perks standards d'arbalète : boost moyen. Une seule application par projectile.
execute unless entity @s[tag=capskills.crossbow_velocity_datapack_boosted] if entity @s[tag=capskills.crossbow.antiarmor.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_35_self
execute unless entity @s[tag=capskills.crossbow_velocity_datapack_boosted] if entity @s[tag=capskills.crossbow.stabilisation.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_35_self
execute unless entity @s[tag=capskills.crossbow_velocity_datapack_boosted] if entity @s[tag=capskills.crossbow.percearmure.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_35_self
execute unless entity @s[tag=capskills.crossbow_velocity_datapack_boosted] if entity @s[tag=capskills.crossbow.carreau_arret.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_35_self

# Maîtrise de l’arbalète : boost modéré seulement si aucun perk plus fort ne l’a déjà accéléré.
execute unless entity @s[tag=capskills.crossbow_velocity_datapack_boosted] if entity @s[tag=capskills.crossbow.mastery.projectile] run function capskills:bridge/combat/crossbow_velocity_scale_1_20_self

# Feedback discret uniquement si le boost datapack a réellement été appliqué.
execute if entity @s[tag=capskills.crossbow_velocity_datapack_boosted] at @s run particle minecraft:crit ~ ~ ~ 0.08 0.08 0.08 0.01 3 force @a[distance=..24]
