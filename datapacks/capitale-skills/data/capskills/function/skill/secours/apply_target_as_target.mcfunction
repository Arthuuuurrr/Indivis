# @s = cible visée. Le lanceur porte le tag capskills.caster.
effect give @s minecraft:instant_health 1 1 true
effect give @s minecraft:regeneration 6 0 true
execute if entity @a[tag=capskills.caster,scores={CAPSK_SEC_RANK=2..},limit=1] run effect give @s minecraft:absorption 18 0 true
execute if entity @a[tag=capskills.caster,scores={CAPSK_SEC_RANK=3..},limit=1] run effect give @s minecraft:regeneration 8 1 true
execute if entity @a[tag=capskills.caster,scores={CAPSK_SUPPORT_RANK=1..},limit=1] run effect give @s minecraft:speed 5 0 true
execute if entity @a[tag=capskills.caster,scores={CAPSK_SUPPORT_RANK=2..},limit=1] run effect give @s minecraft:absorption 14 0 true
execute if entity @a[tag=capskills.caster,scores={CAPSK_SUPPORT_RANK=3..},limit=1] run effect give @s minecraft:resistance 4 0 true
execute if entity @a[tag=capskills.caster,tag=capskills.secours.gestes.r1,limit=1] run effect give @s minecraft:regeneration 7 0 true
execute if entity @a[tag=capskills.caster,tag=capskills.secours.triage.r1,limit=1] run effect give @s minecraft:absorption 10 0 true
execute if entity @a[tag=capskills.caster,tag=capskills.magie.support.r1,limit=1] run effect give @s minecraft:absorption 12 0 true
execute if entity @a[tag=capskills.caster,tag=capskills.magie.support.r2,limit=1] run effect clear @s minecraft:poison
execute if entity @a[tag=capskills.caster,tag=capskills.magie.support.r2,limit=1] run effect clear @s minecraft:weakness
execute if entity @a[tag=capskills.caster,tag=capskills.support.purification.r1,limit=1] run effect clear @s minecraft:slowness
execute if entity @a[tag=capskills.caster,tag=capskills.support.purification.r1,limit=1] run effect clear @s minecraft:mining_fatigue
summon minecraft:area_effect_cloud ~ ~0.05 ~ {Radius:1.8f,Duration:45,WaitTime:0,RadiusPerTick:-0.025f,Color:16262179,potion_contents:{custom_color:16262179},Particle:{type:"minecraft:instant_effect"}}
particle minecraft:heart ~ ~1 ~ 0.8 0.6 0.8 0.04 24 force @a[distance=..18]
particle minecraft:end_rod ~ ~1.0 ~ 0.55 0.55 0.55 0.03 22 force @a[distance=..18]
particle minecraft:happy_villager ~ ~1.1 ~ 0.60 0.45 0.60 0.02 14 force @a[distance=..18]
execute at @a[tag=capskills.caster,limit=1] run particle minecraft:instant_effect ~ ~1.0 ~ 0.35 0.35 0.35 0.02 16 force @a[distance=..18]
playsound minecraft:item.honey_bottle.drink player @s ~ ~ ~ 0.8 1.3 0
title @s actionbar {"text":"Secours impérial reçu.","color":"green"}
scoreboard players set @a[tag=capskills.caster,limit=1] CAPSK_HEAL_CD 8
execute as @a[tag=capskills.caster,limit=1] run title @s actionbar {"text":"Secours ciblé administré.","color":"green"}
execute at @a[tag=capskills.caster,limit=1] run playsound minecraft:block.brewing_stand.brew player @a[tag=capskills.caster,limit=1] ~ ~ ~ 0.8 1.2 0
scoreboard players set #hit CAPSK_TMP 1
