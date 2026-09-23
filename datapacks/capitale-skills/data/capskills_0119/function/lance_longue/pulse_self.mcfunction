tag @s add capskills_0119.lance_longue.caster
scoreboard players set #lance_hit CAPSK_LANCE_RAY 0
execute as @e[tag=capskills_0119.lance_longue.target] if score @s CAPSK_LANCE_OWNER = @a[tag=capskills_0119.lance_longue.caster,limit=1] CAPSK_UID at @s if entity @a[tag=capskills_0119.lance_longue.caster,distance=..8.25,limit=1] run function capskills_0119:lance_longue/pulse_as_target
tag @s remove capskills_0119.lance_longue.caster
execute unless score #lance_hit CAPSK_LANCE_RAY matches 1 run function capskills_0119:lance_longue/cancel_self
