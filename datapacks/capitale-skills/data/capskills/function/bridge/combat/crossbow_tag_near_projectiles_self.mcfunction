
# CapSkills 0.9.29 — tag robuste des projectiles fraîchement tirés à l'arbalète.
# Objectif : les passifs d'arbalète (dont Stabilisation/glowing) ne dépendent jamais de la Salve ni de son cooldown.
# On marque seulement les projectiles proches qui n'ont pas encore reçu leurs effets copiés.

# Flèches vanilla / spectrales, y compris celles déjà taggées par le jar mais non initialisées côté datapack.
execute at @s run tag @e[type=minecraft:arrow,distance=..10,tag=!capskills.crossbow.effects_copied,sort=nearest,limit=4] add capskills.crossbow_fresh_projectile
execute at @s run tag @e[type=minecraft:spectral_arrow,distance=..10,tag=!capskills.crossbow.effects_copied,sort=nearest,limit=4] add capskills.crossbow_fresh_projectile
execute at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow_projectile

# Initialisation âge : permet au fallback de vélocité/impact de fonctionner même si le projectile vient du jar.
scoreboard players set @e[tag=capskills.crossbow_fresh_projectile] CAPSK_CROSS_PROJ_AGE 0

# Passifs d'arbalète — toujours copiés sur chaque projectile frais, sans test CAPSK_CROSS_CD.
execute if entity @s[tag=capskills.maitrise_arbalete.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.mastery.projectile
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.antiarmor.projectile
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.antiarmor.projectile
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.stabilisation.projectile
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.stabilisation.projectile
execute if entity @s[tag=capskills.arbalete.percearmure.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.percearmure.projectile
execute if entity @s[tag=capskills.skill.trait_arbalete_percearmure_1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.percearmure.projectile
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.carreau_arret.projectile
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.carreau_arret.projectile


# Marque de vulnérabilité 0.9.37 — le clic gauche prépare le prochain carreau tiré.
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=1] add capskills.crossbow.vulnerability.projectile
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded,tag=capskills.arbalete.rupture.r1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=1] add capskills.crossbow.rupture.projectile
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded,tag=capskills.skill.trait_arbalete_carreau_rupture_1] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=1] add capskills.crossbow.rupture.projectile
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] at @s if entity @e[tag=capskills.crossbow_fresh_projectile,tag=capskills.crossbow.vulnerability.projectile,distance=..10,limit=1] run function capskills:skill/arbalete/marque_vulnerabilite_loaded_consumed_self

# Salve préparée — uniquement si l'état just_shot est présent. Les autres passifs restent indépendants du cooldown de Salve.
execute if entity @s[tag=capskills.arbalete.salve.just_shot] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow.salve.projectile
execute if entity @s[tag=capskills.arbalete.salve.just_shot] at @s run tag @e[tag=capskills.crossbow_fresh_projectile,distance=..10,sort=nearest,limit=4] add capskills.crossbow_salve_projectile

# Verrouille l'initialisation des projectiles frais pour éviter de recopier les effets à chaque tick de fenêtre.
tag @e[tag=capskills.crossbow_fresh_projectile] add capskills.crossbow.effects_copied
execute at @s if entity @e[tag=capskills.crossbow_fresh_projectile,distance=..10,limit=1] run particle minecraft:crit ~ ~1.1 ~ 0.35 0.20 0.35 0.03 8 force @a[distance=..32]
tag @e[tag=capskills.crossbow_fresh_projectile] remove capskills.crossbow_fresh_projectile
