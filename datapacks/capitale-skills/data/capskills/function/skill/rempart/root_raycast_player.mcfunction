
# 0.9.29 — Raycast Root du Rempart sur entité vivante visée : joueur ou mob.
# @s reste le lanceur via tag capskills.caster ; la fonction scanne devant les yeux.
# Portée indicative : environ 14 blocs.
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.35,tag=!capskills.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/root_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.70 ~ as @e[distance=..1.50,tag=!capskills.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/root_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.80 ~ as @e[distance=..1.60,tag=!capskills.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/root_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-1.55 ~ as @e[distance=..1.70,tag=!capskills.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/root_target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:cave_air unless block ~ ~ ~ minecraft:void_air run scoreboard players set #block CAPSK_TMP 1
scoreboard players add @a[tag=capskills.caster,limit=1] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 if score #block CAPSK_TMP matches 0 as @a[tag=capskills.caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..34 positioned ^ ^ ^0.4 run function capskills:skill/rempart/root_raycast_player
