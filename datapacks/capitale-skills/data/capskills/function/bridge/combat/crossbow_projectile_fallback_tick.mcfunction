# CapSkills 0.9.23 — fallback projectile arbalète précis.
# @s = projectile taggé par le jar / advancement.
# Correction 0.9.23 : la 0.9.21/0.9.22 utilisait un volume trop large autour du projectile,
# ce qui pouvait appliquer les effets à un mob voisin. Le fallback direct/raycast au tir est aussi désactivé.
# Principe : effets uniquement si le carreau passe dans un petit volume centré sur sa position actuelle.
# Si aucun volume précis ne touche, on préfère ne pas appliquer l'effet plutôt que toucher la mauvaise cible.

# Nettoyage des anciens marqueurs au cas où un tick précédent aurait laissé une cible.
tag @e[tag=capskills.crossbow_fallback_target] remove capskills.crossbow_fallback_target

# Passe 1 — noyau très serré autour du carreau : cible réellement traversée / touchée.
execute unless entity @s[tag=capskills.crossbow.impact.done] positioned ~-0.34 ~-0.34 ~-0.34 if entity @e[dx=0.68,dy=0.68,dz=0.68,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] run tag @e[dx=0.68,dy=0.68,dz=0.68,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] add capskills.crossbow_fallback_target

# Passe 2 — marge faible de secours, uniquement si la passe serrée n'a rien trouvé.
# Elle reste beaucoup plus précise que l'ancien volume 2.10 x 2.70 x 2.10.
execute unless entity @s[tag=capskills.crossbow.impact.done] unless entity @e[tag=capskills.crossbow_fallback_target,limit=1] positioned ~-0.52 ~-0.52 ~-0.52 if entity @e[dx=1.04,dy=1.04,dz=1.04,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] run tag @e[dx=1.04,dy=1.04,dz=1.04,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] add capskills.crossbow_fallback_target

# Debug désactivé : execute if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run tellraw @a[distance=..24,limit=1,sort=nearest] {"text":"[CapSkills DBG] fallback projectile précis : cible detectee.","color":"green"}

# Anti-armure : contrôle léger.
execute if entity @s[tag=capskills.crossbow.antiarmor.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.crossbow.antiarmor.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:slowness 2 0 true

# Stabilisation : marquage visuel de la cible.
execute if entity @s[tag=capskills.crossbow.stabilisation.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
execute if entity @s[tag=capskills.crossbow.stabilisation.projectile] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run particle minecraft:instant_effect ~ ~1.2 ~ 0.28 0.20 0.28 0.02 12 force @a[distance=..32]

# Perce-armure : feedback léger.
execute if entity @s[tag=capskills.crossbow.percearmure.projectile] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.22 0.22 0.22 0.02 4 force @a[distance=..32]

# Carreau d'arrêt : anti-charge / contrôle.
execute if entity @s[tag=capskills.crossbow.carreau_arret.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:slowness 3 1 true
execute if entity @s[tag=capskills.crossbow.carreau_arret.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:weakness 3 0 true

# Salve préparée : effet fort, appliqué uniquement au projectile taggé.
execute if entity @s[tag=capskills.crossbow.salve.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
execute if entity @s[tag=capskills.crossbow.salve.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:slowness 4 1 true
execute if entity @s[tag=capskills.crossbow.salve.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run effect give @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] minecraft:weakness 4 1 true
execute if entity @s[tag=capskills.crossbow.salve.projectile] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.1 ~ 0.55 0.35 0.55 0.05 36 force @a[distance=..40]
execute if entity @s[tag=capskills.crossbow.salve.projectile] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run particle minecraft:electric_spark ~ ~1.1 ~ 0.50 0.30 0.50 0.04 28 force @a[distance=..40]
execute if entity @s[tag=capskills.crossbow.salve.projectile] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run playsound minecraft:block.anvil.land player @a[distance=..40] ~ ~ ~ 0.50 1.25 0


# Marque de vulnérabilité 0.9.37 : le projectile préparé applique l'effet à la cible réellement touchée.
execute if entity @s[tag=capskills.crossbow.vulnerability.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] as @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] at @s run function capskills:skill/arbalete/marque_vulnerabilite_impact_as_target
execute if entity @s[tag=capskills.crossbow.rupture.projectile] if entity @e[tag=capskills.crossbow_fallback_target,limit=1] as @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] at @s run function capskills:skill/arbalete/carreau_rupture_impact_as_target

execute if entity @e[tag=capskills.crossbow_fallback_target,limit=1] at @e[tag=capskills.crossbow_fallback_target,limit=1,sort=nearest] run function capskills:visual/activation/arbalete_impact_self
execute if entity @e[tag=capskills.crossbow_fallback_target,limit=1] run tag @s add capskills.crossbow.impact.done
tag @e[tag=capskills.crossbow_fallback_target] remove capskills.crossbow_fallback_target
execute if entity @s[tag=capskills.crossbow.impact.done] run function capskills:bridge/combat/crossbow_projectile_cleanup_self
