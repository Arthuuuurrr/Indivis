# Raycast Baguette de Destruction — 0.8.10 HEALTH_SCAN_VISUAL.
# @s = lanceur pour la fonction initiale ; les recursions conservent la position du rayon.
# Objectif : ne plus dependre des tags entity_type 1.21.x, qui ont cause des faux negatifs.

# Trainee visible, meme si aucune cible n'est touchee.
execute if score #hit CAPSK_TMP matches 0 run particle minecraft:end_rod ~ ~ ~ 0.030 0.030 0.030 0.00 1 force @a[distance=..32]
execute if score #hit CAPSK_TMP matches 0 run particle minecraft:witch ~ ~ ~ 0.060 0.060 0.060 0.00 5 force @a[distance=..32]
execute if score #hit CAPSK_TMP matches 0 run particle minecraft:dragon_breath ~ ~ ~ 0.040 0.040 0.040 0.00 2 force @a[distance=..32]

# Detection genereuse autour du point du rayon.
# On ne filtre plus par type=#tag : on teste seulement si l'entite possede une donnee Health.
# Cela cible les entites vivantes, y compris les mobs et la plupart des PNJ moddes.
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.80,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.75 ~ as @e[distance=..1.95,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.75 ~ as @e[distance=..1.95,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_target_as_target

# Pas suivant : avance dans la direction du regard du lanceur.
scoreboard players add @a[tag=capskills.caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 as @a[tag=capskills.caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..80 positioned ^ ^ ^0.45 run function capskills:skill/magie/raycast_target
