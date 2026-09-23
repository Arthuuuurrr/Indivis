function capskills:integration/ensure_current_self
execute if score @s CAPSK_MAG_DOT_CD matches 1.. run title @s actionbar {"text":"Marque corrosive en recharge.","color":"light_purple"}
execute if score @s CAPSK_MAG_DOT_CD matches 0 run scoreboard players set #hit CAPSK_TMP 0
execute if score @s CAPSK_MAG_DOT_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if score @s CAPSK_MAG_DOT_CD matches 0 run tag @e[tag=capskills.magic_dot_target] remove capskills.magic_dot_target
execute if score @s CAPSK_MAG_DOT_CD matches 0 run tag @s add capskills.caster
execute if score @s CAPSK_MAG_DOT_CD matches 0 anchored eyes rotated as @s positioned ^ ^ ^0.65 run function capskills:skill/magie/raycast_dot_target
execute if score @s CAPSK_MAG_DOT_CD matches 0 run tag @s remove capskills.caster
execute if score @s CAPSK_MAG_DOT_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/magie/apply_dot_from_caster_self
execute if score @s CAPSK_MAG_DOT_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Aucune cible pour la Marque corrosive.","color":"yellow"}
tag @e[tag=capskills.magic_dot_target] remove capskills.magic_dot_target
