# CapSkills 0.9.109 — relâchement en trois rotations complètes synchronisées.
tag @s remove capskills.lame.tourbillon.channeling
tag @s remove capskills.lame.tourbillon.armed
tag @s add capskills.lame.tourbillon.spinning
tag @s add capskills.lame.tourbillon.animation_pending
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 3
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 0
scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 78
scoreboard players set @s CAPSK_LAME_TOURB_PULSE 0
scoreboard players set @s CAPSK_LAME_TOURB_TARGETS 0
scoreboard players set @s CAPSK_LAME_TOURB_CD 32
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:resistance 4 0 true
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 1.00 0.70 0
execute at @s run particle minecraft:explosion ~ ~1.0 ~ 0.20 0.20 0.20 0.00 1 force @a[distance=..24]
title @s actionbar {"text":"Tourbillon impérial : trois rotations complètes. Recharge : 32 s.","color":"red"}
