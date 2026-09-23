# 0.8.18 — premier point Lame : équipement de départ.
execute unless entity @s[tag=capskills.item.training_sword_given] run function capskills:give/starter/training_sword_self
tag @s add capskills.item.training_sword_given
