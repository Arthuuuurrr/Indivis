tag @s remove capskills_0119.lance_longue.channeling
tag @s remove capskills_0119.lance_longue.active
tag @s remove capskills_0119.lance_longue.caster
function capskills_0119:lance_longue/cleanup_target_self
scoreboard players set @s CAPSK_LANCE_PHASE 0
scoreboard players set @s CAPSK_LANCE_RAY 0
