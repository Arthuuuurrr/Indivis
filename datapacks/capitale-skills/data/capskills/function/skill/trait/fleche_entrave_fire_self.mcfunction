# @s = archer ; applique sur la cible regardée au moment du tir.
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @e[tag=capskills.trait_target] remove capskills.trait_target
tag @s add capskills.trait_caster
execute anchored eyes rotated as @s positioned ^ ^ ^1.0 run function capskills:skill/trait/raycast_target_silent
execute if score #hit CAPSK_TMP matches 1.. as @e[tag=capskills.trait_target,limit=1,sort=nearest] at @s run function capskills:skill/trait/fleche_entrave_impact_as_target
execute if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Flèche d’entrave tirée, aucune cible verrouillée.","color":"yellow"}
tag @s remove capskills.trait_caster
tag @e[tag=capskills.trait_target] remove capskills.trait_target
tag @s remove capskills.trait.fleche_entrave.loaded
scoreboard players set @s CAPSK_BOW_ENTRAVE_T 0
