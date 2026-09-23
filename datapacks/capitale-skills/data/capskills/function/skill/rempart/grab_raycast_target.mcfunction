# CapSkills 0.9.53 — Poigne du Rempart : raycast élargi pour coller aux hitbox joueurs/mobs.
# @s = position de scan ; lanceur = joueur taggé capskills.pull_caster.
# Trois volumes sont testés autour du rayon : centre, haut du corps, bas du corps.
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..2.05,tag=!capskills.pull_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/grab_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.85 ~ as @e[distance=..2.20,tag=!capskills.pull_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/grab_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.85 ~ as @e[distance=..2.20,tag=!capskills.pull_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/grab_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:cave_air unless block ~ ~ ~ minecraft:void_air run scoreboard players set #block CAPSK_TMP 1
scoreboard players add @a[tag=capskills.pull_caster,limit=1] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 if score #block CAPSK_TMP matches 0 as @a[tag=capskills.pull_caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..36 positioned ^ ^ ^0.45 run function capskills:skill/rempart/grab_raycast_target
