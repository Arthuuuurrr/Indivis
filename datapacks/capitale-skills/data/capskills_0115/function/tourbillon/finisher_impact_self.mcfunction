# Impact lourd final : +3 dégâts dans une zone élargie, sans modifier le budget total de la 1.5.9.
scoreboard players add @s CAPSK_LAME_TOURB_PULSE 1
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 8
tag @s add capskills_0115.tourbillon.caster
execute at @s run particle minecraft:explosion ~ ~0.20 ~ 0.45 0.15 0.45 0.00 4 force @a[distance=..32]
execute at @s run particle minecraft:cloud ~ ~0.10 ~ 3.10 0.18 3.10 0.05 42 force @a[distance=..32]
execute at @s run particle minecraft:crit ~ ~0.55 ~ 2.65 0.35 2.65 0.05 26 force @a[distance=..32]
execute at @s run playsound bettercombat:hammer_slam player @a[distance=..32] ~ ~ ~ 1.20 0.82 0
execute at @s run playsound minecraft:entity.generic.explode player @a[distance=..32] ~ ~ ~ 0.65 1.15 0
execute at @s as @e[distance=0.35..4.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run scoreboard players add @a[tag=capskills_0115.tourbillon.caster,limit=1,sort=nearest] CAPSK_LAME_TOURB_TARGETS 1
execute at @s as @e[distance=0.35..4.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run damage @s 3.0 minecraft:generic by @a[tag=capskills_0115.tourbillon.caster,limit=1,sort=nearest]
execute at @s as @e[distance=0.35..4.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:slowness 2 1 true
execute at @s as @e[distance=0.35..4.25,tag=!capskills_0115.tourbillon.caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:weakness 2 0 true
tag @s remove capskills_0115.tourbillon.caster
