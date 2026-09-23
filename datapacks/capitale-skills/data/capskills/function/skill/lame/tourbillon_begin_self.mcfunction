# CapSkills 0.9.78 EXP — Tourbillon : begin fiable, appelé après validation skill/CD.
tag @s remove capskills.lame.tourbillon.armed
tag @s add capskills.lame.tourbillon.channeling
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 16
scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 0
scoreboard players set @s CAPSK_LAME_TOURB_PULSE 0
scoreboard players set @s CAPSK_LAME_TOURB_TARGETS 0
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 1
effect give @s minecraft:slowness 1 3 true
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 0.75 0.05 0.75 0.00 5 force @a[distance=..20]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.55 0.30 0.55 0.02 10 force @a[distance=..20]
execute at @s run playsound minecraft:block.beacon.activate player @a[distance=..20] ~ ~ ~ 0.60 1.45 0
title @s actionbar {"text":"Tourbillon : canalisation — relâchement dans 16 ticks.","color":"red"}
