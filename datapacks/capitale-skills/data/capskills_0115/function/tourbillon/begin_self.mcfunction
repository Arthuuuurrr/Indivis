# Nettoyage de toutes les anciennes implémentations avant d'armer l'horloge 1.6.0.
tag @s remove capskills.lame.tourbillon.channeling
tag @s remove capskills.lame.tourbillon.spinning
tag @s remove capskills.lame.tourbillon.animation_pending
tag @s remove capskills.bc.anim.tourbillon_360_1
tag @s remove capskills.bc.anim.tourbillon_360_2
tag @s remove capskills.bc.anim.tourbillon_360_3
tag @s remove capskills_0110.tourbillon.channeling
tag @s remove capskills_0110.tourbillon.spinning
tag @s remove capskills_0111.tourbillon.channeling
tag @s remove capskills_0111.tourbillon.spinning
tag @s remove capskills_0111.tourbillon.caster
tag @s remove capskills_0113.tourbillon.channeling
tag @s remove capskills_0113.tourbillon.spinning
tag @s remove capskills_0113.tourbillon.caster
tag @s remove capskills_0114.tourbillon.channeling
tag @s remove capskills_0114.tourbillon.spinning
tag @s remove capskills_0114.tourbillon.caster
tag @s remove capskills_0114.tourbillon.heavy_finisher
tag @s remove capskills_0115.tourbillon.spinning
tag @s remove capskills_0115.tourbillon.heavy_finisher
tag @s add capskills_0115.tourbillon.channeling
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 16
scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 0
scoreboard players set @s CAPSK_LAME_TOURB_PULSE 0
scoreboard players set @s CAPSK_LAME_TOURB_TARGETS 0
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 1
effect give @s minecraft:slowness 1 3 true
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 0.75 0.05 0.75 0.00 5 force @a[distance=..20]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.55 0.30 0.55 0.02 10 force @a[distance=..20]
execute at @s run playsound minecraft:block.beacon.activate player @a[distance=..20] ~ ~ ~ 0.60 1.45 0
title @s actionbar {"text":"Tourbillon : canalisation — 16 ticks.","color":"red"}
