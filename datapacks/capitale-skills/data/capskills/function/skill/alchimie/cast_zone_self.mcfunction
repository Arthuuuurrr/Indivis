function capskills:integration/ensure_current_self
execute if score @s CAPSK_ALCH_CD matches 1.. run title @s actionbar {"text":"Altération des Seuils en recharge.","color":"dark_green"}
execute if score @s CAPSK_ALCH_CD matches 0 run scoreboard players set #block CAPSK_TMP 0
execute if score @s CAPSK_ALCH_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if score @s CAPSK_ALCH_CD matches 0 run tag @s add capskills.zone_caster
execute if score @s CAPSK_ALCH_CD matches 0 anchored eyes rotated as @s positioned ^ ^ ^1.0 run function capskills:skill/alchimie/raycast_zone_ground
execute if score @s CAPSK_ALCH_CD matches 0 run tag @s remove capskills.zone_caster
execute if score @s CAPSK_ALCH_CD matches 0 if score #block CAPSK_TMP matches 0 run function capskills:skill/alchimie/apply_zone_here_self
