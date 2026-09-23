# Raycast monocible corrigé 0.9.121.
# Le sélecteur radial précédent comparait le point à hauteur des yeux à la position
# des pieds de l'entité et ratait donc presque toutes les cibles debout.
# Cette boîte étroite suit l'axe du regard et couvre la hauteur réelle des hitbox.
# Le point initial est créé depuis les yeux puis l’ancre est repassée sur FEET.
# Ainsi chaque récursion locale ^ ^ ^0.45 avance depuis le point précédent,
# au lieu de repartir des yeux du lanceur à chaque étape.
execute if block ~ ~ ~ minecraft:air positioned ~-0.85 ~-1.85 ~-0.85 as @e[dx=1.70,dy=2.70,dz=1.70,sort=nearest,limit=1,tag=!capskills_0119.lance_longue.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run function capskills_0119:lance_longue/mark_target_as_target
execute unless score #lance_hit CAPSK_LANCE_RAY matches 1 if score @s CAPSK_LANCE_RAY matches ..14 if block ~ ~ ~ minecraft:air positioned ^ ^ ^0.45 run function capskills_0119:lance_longue/raycast_continue
execute if block ~ ~ ~ minecraft:cave_air positioned ~-0.85 ~-1.85 ~-0.85 as @e[dx=1.70,dy=2.70,dz=1.70,sort=nearest,limit=1,tag=!capskills_0119.lance_longue.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run function capskills_0119:lance_longue/mark_target_as_target
execute unless score #lance_hit CAPSK_LANCE_RAY matches 1 if score @s CAPSK_LANCE_RAY matches ..14 if block ~ ~ ~ minecraft:cave_air positioned ^ ^ ^0.45 run function capskills_0119:lance_longue/raycast_continue
execute if block ~ ~ ~ minecraft:void_air positioned ~-0.85 ~-1.85 ~-0.85 as @e[dx=1.70,dy=2.70,dz=1.70,sort=nearest,limit=1,tag=!capskills_0119.lance_longue.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run function capskills_0119:lance_longue/mark_target_as_target
execute unless score #lance_hit CAPSK_LANCE_RAY matches 1 if score @s CAPSK_LANCE_RAY matches ..14 if block ~ ~ ~ minecraft:void_air positioned ^ ^ ^0.45 run function capskills_0119:lance_longue/raycast_continue
