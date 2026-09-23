# 0.9.35 — premier point Ombre : équipement de départ + Sceau des Ombres.
execute unless entity @s[tag=capskills.item.training_sword_given] run function capskills:give/starter/training_sword_self
tag @s add capskills.item.training_sword_given
execute unless entity @s[tag=capskills.item.sceau_ombres_given] run function capskills:give/mod/sceau_ombres_self
tag @s add capskills.item.sceau_ombres_given
tag @s add capskills.ombre.r1
