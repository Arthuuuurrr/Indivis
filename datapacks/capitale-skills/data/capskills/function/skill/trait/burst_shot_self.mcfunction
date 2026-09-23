# @s = lanceur. Chaque impact refait un raycast instantané depuis la visée actuelle : pas de projectile lent/falling arrow.
scoreboard players add @s CAPSK_RAFALE_BURST_SHOT 1
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
function capskills:visual/rafale_tracer_self
tag @e[tag=capskills.trait_target] remove capskills.trait_target
tag @s add capskills.trait_caster
execute at @s anchored eyes rotated as @s positioned ^ ^ ^0.75 run function capskills:skill/trait/raycast_target
execute if score #hit CAPSK_TMP matches 1.. run function capskills:skill/trait/apply_rafale_impact_from_caster_self
execute if score #hit CAPSK_TMP matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^2.0 run particle minecraft:smoke ~ ~ ~ 0.08 0.08 0.08 0.01 6 force @a[distance=..32]
execute if score #hit CAPSK_TMP matches 0 if score @s CAPSK_RAFALE_BURST_SHOT matches 1 run title @s actionbar {"text":"Rafale : aucune cible dans l’axe.","color":"yellow"}
tag @s remove capskills.trait_caster
tag @e[tag=capskills.trait_target] remove capskills.trait_target
