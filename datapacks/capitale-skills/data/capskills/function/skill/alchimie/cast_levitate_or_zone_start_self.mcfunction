function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.alteration.levitate.r1] run function capskills:skill/alchimie/cast_zone_self
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 1.. run title @s actionbar {"text":"Suspension des Seuils en recharge.","color":"dark_green"}
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 run scoreboard players set #hit CAPSK_TMP 0
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 run tag @e[tag=capskills.alter_target] remove capskills.alter_target
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 run tag @s add capskills.caster
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 anchored eyes rotated as @s positioned ^ ^ ^0.75 run function capskills:skill/alchimie/raycast_target
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 run tag @s remove capskills.caster
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/alchimie/apply_levitate_from_caster_self
execute if entity @s[tag=capskills.alteration.levitate.r1] if score @s CAPSK_ALTER_LEV_CD matches 0 if score #hit CAPSK_TMP matches 0 run function capskills:skill/alchimie/cast_zone_self
tag @e[tag=capskills.alter_target] remove capskills.alter_target
