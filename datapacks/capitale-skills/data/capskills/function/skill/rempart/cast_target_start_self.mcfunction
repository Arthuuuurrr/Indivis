# LEGACY 0.8.4 : ancien usage non-sneak du Relais du Rempart.
# LEGACY : ancien usage de ciblage. Depuis 0.9.27, l’Égide ciblée est retirée.
# Le chemin actif du Relais utilise cast_target_or_zone_start_self pour la zone.
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @s add capskills.caster
execute rotated as @s anchored eyes positioned ^ ^ ^0.6 run function capskills:skill/rempart/raycast_target
tag @s remove capskills.caster
execute if score #hit CAPSK_TMP matches 0 if score @s CAPSK_REMP_SELF_CD matches 1.. run title @s actionbar {"text":"Rempart personnel en recharge.","color":"blue"}
execute if score #hit CAPSK_TMP matches 0 if score @s CAPSK_REMP_SELF_CD matches 0 run function capskills:skill/rempart/apply_self_self
