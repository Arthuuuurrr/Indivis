# 0.9.0-rc2 — unlock Pas déphasé : tag + premier Sceau des Ombres.
tag @s add capskills.ombre.shadowstep.r1
execute unless entity @s[tag=capskills.item.sceau_ombres_given] run function capskills:give/mod/sceau_ombres_self
tag @s add capskills.item.sceau_ombres_given
