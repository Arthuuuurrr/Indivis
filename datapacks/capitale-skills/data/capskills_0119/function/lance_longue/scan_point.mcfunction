# Segment de la boîte frontale : 3,60 x 3,60 x 3,60.
# La sélection n'exige plus la présence du champ NBT Health, afin d'accepter
# aussi les LivingEntity modées dont la sérialisation diffère.
execute positioned ~-1.80 ~-1.80 ~-1.80 as @e[dx=3.60,dy=3.60,dz=3.60,sort=nearest,limit=1,tag=!capskills_0119.lance_longue.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] run function capskills_0119:lance_longue/mark_target_as_target
