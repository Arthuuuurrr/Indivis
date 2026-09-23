# CapSkills 0.9.19 — fallback direct arbalète indépendant du projectile.
# @s = arbalétrier. Déclenché par minecraft.used:minecraft.crossbow.
# But : confirmer les effets via un raycast depuis le regard, même si les hooks Java projectile ne passent pas.
function capskills:integration/ensure_current_self
scoreboard players set @s CAPSK_CROSS_DIRECT_HIT 0
tag @s remove capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.percearmure.r1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.skill.trait_arbalete_percearmure_1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.salve.ready] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.salve.pending_clear] run tag @s add capskills.crossbow_direct_allowed
execute if entity @s[tag=capskills.arbalete.salve.just_shot] run tag @s add capskills.crossbow_direct_allowed
scoreboard players set @s CAPSK_RAY_STEP 0
execute if entity @s[tag=capskills.crossbow_direct_allowed] run tag @s add capskills.crossbow_direct_caster
execute if entity @s[tag=capskills.crossbow_direct_allowed] anchored eyes positioned ^ ^ ^0.75 run function capskills:bridge/combat/crossbow_direct_raycast_step
# 0.9.22 debug désactivé : execute if entity @s[tag=capskills.crossbow_direct_allowed] if score @s CAPSK_CROSS_DIRECT_HIT matches 1.. run tellraw @s {"text":"[CapSkills DBG] fallback arbalète direct : cible marquée.","color":"dark_aqua"}
# 0.9.22 debug désactivé : execute if entity @s[tag=capskills.crossbow_direct_allowed] if score @s CAPSK_CROSS_DIRECT_HIT matches 0 run tellraw @s {"text":"[CapSkills DBG] fallback arbalète direct : aucune cible dans l'axe.","color":"gray"}
tag @s remove capskills.crossbow_direct_caster
tag @s remove capskills.crossbow_direct_allowed
