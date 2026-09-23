# 0.9.35 — premier perk Arbalète : tag fonctionnel + arbalète d'entraînement.
tag @s add capskills.arbalete.antiarmor.r1
execute unless entity @s[tag=capskills.item.training_crossbow_given] run function capskills:give/starter/training_crossbow_self
tag @s add capskills.item.training_crossbow_given
