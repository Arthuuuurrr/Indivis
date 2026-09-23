function capskills:integration/ensure_current_self
execute unless score @s CAPSK_EGIDE_RANK matches 1.. run title @s actionbar {"text":"Égide de zone non débloquée : prenez Égide I.","color":"red"}
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 1.. run title @s actionbar {"text":"Égide de zone en recharge.","color":"blue"}
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 run scoreboard players set #block CAPSK_TMP 0
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 run tag @s add capskills.zone_caster
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 anchored eyes rotated as @s positioned ^ ^ ^1.0 run function capskills:skill/rempart/raycast_zone_ground
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 run tag @s remove capskills.zone_caster
execute if score @s CAPSK_EGIDE_RANK matches 1.. if score @s CAPSK_REMP_ZONE_CD matches 0 if score #block CAPSK_TMP matches 0 run function capskills:skill/rempart/apply_zone_here_self
