# Six impacts de zone synchronisés aux offsets +5 et +10 de chaque rotation.
# 6 x 1,5 = 9 dégâts de base, identiques au total de la 0.9.110.
# Le bonus lourd de +3 est déplacé intégralement vers la frappe au sol finale.
scoreboard players add @s CAPSK_LAME_TOURB_PULSE 1
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 5
tag @s add capskills_0115.tourbillon.caster
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 2.35 0.22 2.35 0.00 10 force @a[distance=..24]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 1.95 0.28 1.95 0.02 8 force @a[distance=..24]
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 0.48 0.86 0
execute at @s as @e[distance=0.35..3.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run scoreboard players add @a[tag=capskills_0115.tourbillon.caster,limit=1,sort=nearest] CAPSK_LAME_TOURB_TARGETS 1
execute at @s as @e[distance=0.35..3.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run damage @s 1.5 minecraft:generic by @a[tag=capskills_0115.tourbillon.caster,limit=1,sort=nearest]
execute at @s as @e[distance=0.35..3.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:slowness 1 0 true
execute at @s as @e[distance=0.35..3.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:glowing 1 0 true
execute if items entity @s weapon.mainhand #capskills:weapon/short_impact at @s as @e[distance=0.35..3.10,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:weakness 2 0 true
tag @s remove capskills_0115.tourbillon.caster
