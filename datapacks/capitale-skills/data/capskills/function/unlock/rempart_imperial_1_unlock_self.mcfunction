# 0.8.13 — unlock Rempart : tag + premier Relais du Rempart.
tag @s add capskills.rempart.r1
execute unless entity @s[tag=capskills.item.relais_rempart_given] run function capskills:give/mod/relais_rempart_self
tag @s add capskills.item.relais_rempart_given
execute unless entity @s[tag=capskills.item.training_shield_given] run function capskills:give/starter/training_shield_self
tag @s add capskills.item.training_shield_given
