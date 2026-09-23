# @s = lanceur. Canalisation vitale personnelle.
effect give @s minecraft:instant_health 1 0 true
effect give @s minecraft:regeneration 4 0 true
execute if entity @s[tag=capskills.secours.gestes.r1] run effect give @s minecraft:regeneration 5 0 true
execute if entity @s[tag=capskills.magie.support.r1] run effect give @s minecraft:absorption 12 0 true
execute if entity @s[tag=capskills.magie.support.r2] run effect clear @s minecraft:poison
execute if entity @s[tag=capskills.magie.support.r2] run effect clear @s minecraft:weakness
execute if entity @s[tag=capskills.support.purification.r1] run effect clear @s minecraft:slowness
execute if entity @s[tag=capskills.support.purification.r1] run effect clear @s minecraft:mining_fatigue
summon minecraft:area_effect_cloud ~ ~0.05 ~ {Radius:1.4f,Duration:35,WaitTime:0,RadiusPerTick:-0.025f,Color:16262179,potion_contents:{custom_color:16262179},Particle:{type:"minecraft:instant_effect"}}
particle minecraft:heart ~ ~1 ~ 0.6 0.5 0.6 0.04 18 force @s
particle minecraft:happy_villager ~ ~0.2 ~ 0.8 0.05 0.8 0.02 30 force @s
particle minecraft:happy_villager ~ ~1.1 ~ 0.45 0.45 0.45 0.02 12 force @s
particle minecraft:composter ~ ~0.6 ~ 0.45 0.20 0.45 0.05 16 force @s
playsound minecraft:item.honey_bottle.drink player @s ~ ~ ~ 0.7 1.2 0
scoreboard players set @s CAPSK_SELF_CD 45
title @s actionbar {"text":"Baguette de Soins : canalisation vitale. Recharge : 45 s.","color":"green"}
