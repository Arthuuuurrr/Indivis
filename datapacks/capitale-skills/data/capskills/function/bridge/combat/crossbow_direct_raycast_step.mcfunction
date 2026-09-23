# CapSkills 0.9.21 — étape de raycast arbalète corrigée.
# @s = arbalétrier ; position = point courant du rayon.
# Correction : les anciens sélecteurs distance= visaient les pieds de l'entité.
# Ici on utilise dx/dy/dz pour intercepter la hitbox Java autour du point de rayon.
scoreboard players add @s CAPSK_RAY_STEP 1
execute if score @s CAPSK_CROSS_DIRECT_HIT matches 0 positioned ~-0.80 ~-1.20 ~-0.80 if entity @e[tag=!capskills.crossbow_direct_caster,dx=1.60,dy=2.40,dz=1.60,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] run tag @e[tag=!capskills.crossbow_direct_caster,dx=1.60,dy=2.40,dz=1.60,limit=1,sort=nearest,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker] add capskills.crossbow_direct_target
execute if score @s CAPSK_CROSS_DIRECT_HIT matches 0 if entity @e[tag=capskills.crossbow_direct_target,limit=1] run function capskills:bridge/combat/crossbow_direct_apply_self
execute if score @s CAPSK_CROSS_DIRECT_HIT matches 0 if score @s CAPSK_RAY_STEP matches ..42 unless entity @e[tag=capskills.crossbow_direct_target,limit=1] positioned ^ ^ ^0.75 run function capskills:bridge/combat/crossbow_direct_raycast_step
