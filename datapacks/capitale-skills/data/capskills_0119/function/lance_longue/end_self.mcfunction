tag @s remove capskills_0119.lance_longue.channeling
tag @s remove capskills_0119.lance_longue.active
tag @s remove capskills_0119.lance_longue.caster
function capskills_0119:lance_longue/cleanup_target_self
scoreboard players set @s CAPSK_LANCE_PHASE 3
scoreboard players set @s CAPSK_LANCE_RAY 0
execute at @s run particle minecraft:cloud ~ ~0.7 ~ 0.30 0.12 0.30 0.01 6 force @a[distance=..20]
