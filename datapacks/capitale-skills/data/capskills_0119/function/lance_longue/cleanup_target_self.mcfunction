tag @s add capskills_0119.lance_longue.caster
execute as @e[tag=capskills_0119.lance_longue.target] if score @s CAPSK_LANCE_OWNER = @a[tag=capskills_0119.lance_longue.caster,limit=1] CAPSK_UID run function capskills_0119:lance_longue/cleanup_target_as_target
tag @s remove capskills_0119.lance_longue.caster
tag @s remove capskills_0119.lance_longue.target_locked
