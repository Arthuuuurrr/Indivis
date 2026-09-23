# Acquisition frontale dérivée de Better Combat FORWARD_BOX.
# Portée 7,20 : largeur 3,60 ; hauteur 3,60 ; profondeur 7,20.
# Trois volumes contigus centrés à 1,80 / 3,60 / 5,40 blocs.
execute unless score @s CAPSK_UID matches 1.. run function capskills:integration/assign_uid_self
function capskills_0119:lance_longue/cleanup_target_self
tag @s add capskills_0119.lance_longue.caster
scoreboard players set @s CAPSK_LANCE_RAY 0
tag @s remove capskills_0119.lance_longue.target_locked
tag @s remove capskills_0119.lance_longue.last_no_target

execute unless entity @s[tag=capskills_0119.lance_longue.target_locked] anchored eyes positioned ^ ^ ^1.80 run function capskills_0119:lance_longue/scan_point
execute unless entity @s[tag=capskills_0119.lance_longue.target_locked] anchored eyes positioned ^ ^ ^3.60 run function capskills_0119:lance_longue/scan_point
execute unless entity @s[tag=capskills_0119.lance_longue.target_locked] anchored eyes positioned ^ ^ ^5.40 run function capskills_0119:lance_longue/scan_point

tag @s remove capskills_0119.lance_longue.caster
execute if entity @s[tag=capskills_0119.lance_longue.target_locked] run function capskills_0119:lance_longue/begin_self
execute unless entity @s[tag=capskills_0119.lance_longue.target_locked] run function capskills_0119:lance_longue/no_target_self
