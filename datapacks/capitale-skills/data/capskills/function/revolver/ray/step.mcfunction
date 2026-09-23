scoreboard players add @s CAPREV_RAY 1
# Une particule tous les deux pas (0,5 bloc) pour limiter le coût visuel.
execute if score @s CAPREV_RAY matches 2.. run particle minecraft:crit ~ ~ ~ 0 0 0 0 1 force
# dx/dy/dz sélectionne les entités dont la HITBOX intersecte le volume au point du rayon.
# Le volume est centré sur le rayon afin d'éviter le biais vers les coordonnées positives.
execute if score @s CAPREV_HIT matches 0 if block ~ ~ ~ #capskills:revolver_passable positioned ~-0.5 ~-0.5 ~-0.5 as @e[dx=0,dy=0,dz=0,sort=nearest,limit=1,tag=!capskills.revolver.shooter,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:armor_stand,type=!minecraft:marker,type=!minecraft:interaction,type=!minecraft:arrow,type=!minecraft:spectral_arrow,type=!minecraft:trident,type=!minecraft:firework_rocket,type=!minecraft:item_frame,type=!minecraft:glow_item_frame,type=!minecraft:painting] run function capskills:revolver/ray/hit_as_target
execute if score @s CAPREV_HIT matches 0 unless block ~ ~ ~ #capskills:revolver_passable run particle minecraft:smoke ~ ~ ~ 0.04 0.04 0.04 0.01 4 force
execute if score @s CAPREV_HIT matches 0 if block ~ ~ ~ #capskills:revolver_passable if score @s CAPREV_RAY < @s CAPREV_RANGE positioned ^ ^ ^0.25 run function capskills:revolver/ray/step
