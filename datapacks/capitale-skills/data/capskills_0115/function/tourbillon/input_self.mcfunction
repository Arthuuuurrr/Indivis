# Entrée exclusive Tourbillon : sneak + clic droit.
# Lance longue possède désormais son propre payload sprint + clic droit.
execute if predicate capskills_0115:is_sneaking unless entity @s[tag=capskills.dual_wield.active] unless items entity @s weapon.offhand #capskills_0132:dual_wield_one_handed if items entity @s weapon.mainhand #capskills_0115:tourbillon_compatible run function capskills_0115:tourbillon/start_validated_self
