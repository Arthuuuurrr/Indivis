# CapSkills 0.9.78 EXP — pulsation Tourbillon robuste. @s = lanceur.
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 5
scoreboard players set @s CAPSK_LAME_TOURB_TARGETS 0
tag @s add capskills.tourbillon_caster
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 2.35 0.25 2.35 0.00 18 force @a[distance=..24]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 1.95 0.30 1.95 0.02 14 force @a[distance=..24]
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 0.75 0.82 0
execute at @s run playsound minecraft:entity.player.attack.strong player @a[distance=..24] ~ ~ ~ 0.35 0.70 0
# Comptage puis dégâts. On reste volontairement large : toute entité vivante proche, hors caster/objets techniques.
execute at @s as @e[distance=0.35..3.25,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run scoreboard players add @a[tag=capskills.tourbillon_caster,limit=1,sort=nearest] CAPSK_LAME_TOURB_TARGETS 1
execute at @s as @e[distance=0.35..3.25,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run damage @s 3 minecraft:generic by @a[tag=capskills.tourbillon_caster,limit=1,sort=nearest]
execute at @s as @e[distance=0.35..3.25,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:slowness 1 0 true
execute at @s as @e[distance=0.35..3.25,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:glowing 1 0 true
# Claymore et autres armes lourdes : elles sont explicitement traitées comme compatibles Tourbillon, même sans angle 360 natif.
execute if items entity @s weapon.mainhand #capskills:weapon/twohand_heavy at @s as @e[distance=0.35..3.75,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run damage @s 1 minecraft:generic by @a[tag=capskills.tourbillon_caster,limit=1,sort=nearest]
execute if items entity @s weapon.mainhand #capskills:weapon/short_impact at @s as @e[distance=0.35..3.10,tag=!capskills.tourbillon_caster,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand] if data entity @s Health run effect give @s minecraft:weakness 2 0 true
execute if items entity @s weapon.mainhand #capskills:weapon/onehand_axe at @s run particle minecraft:crit ~ ~1.0 ~ 1.85 0.25 1.85 0.03 12 force @a[distance=..24]
execute if score @s CAPSK_LAME_TOURB_TARGETS matches 1.. run title @s actionbar [{"text":"Tourbillon : cibles touchées ","color":"red"},{"score":{"name":"@s","objective":"CAPSK_LAME_TOURB_TARGETS"},"color":"red"}]
tag @s remove capskills.tourbillon_caster
