# Début de la phase active, déclenché exactement au tick 16 par le jar 1.6.0.
tag @s remove capskills_0115.tourbillon.channeling
tag @s add capskills_0115.tourbillon.spinning
tag @s remove capskills_0115.tourbillon.heavy_finisher
execute if items entity @s weapon.mainhand #capskills_0115:ground_finisher_compatible run tag @s add capskills_0115.tourbillon.heavy_finisher
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 3
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 0
scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 46
execute if entity @s[tag=capskills_0115.tourbillon.heavy_finisher] run scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 62
scoreboard players set @s CAPSK_LAME_TOURB_PULSE 0
scoreboard players set @s CAPSK_LAME_TOURB_TARGETS 0
scoreboard players set @s CAPSK_LAME_TOURB_CD 32
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:resistance 4 0 true
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 1.00 0.70 0
execute at @s run particle minecraft:explosion ~ ~1.0 ~ 0.20 0.20 0.20 0.00 1 force @a[distance=..24]
execute unless entity @s[tag=capskills_0115.tourbillon.heavy_finisher] run title @s actionbar {"text":"Tourbillon impérial : trois rotations en rafale continue — six impacts.","color":"red"}
execute if entity @s[tag=capskills_0115.tourbillon.heavy_finisher] run title @s actionbar {"text":"Tourbillon lourd : trois rotations — frappe au sol finale.","color":"gold"}
