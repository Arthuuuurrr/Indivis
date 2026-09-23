# 0.8.18 — premier point Trait : équipement de départ.
execute unless entity @s[tag=capskills.item.training_bow_given] run function capskills:give/starter/training_bow_self
tag @s add capskills.item.training_bow_given
