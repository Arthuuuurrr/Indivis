# Validation silencieuse pendant tout le lancement.
execute unless items entity @s weapon.mainhand #capskills_0115:tourbillon_compatible run function capskills_0115:tourbillon/cancel_self
execute if entity @s[tag=capskills_0115.tourbillon.heavy_finisher] unless items entity @s weapon.mainhand #capskills_0115:ground_finisher_compatible run function capskills_0115:tourbillon/cancel_self
