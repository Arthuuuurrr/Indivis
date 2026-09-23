# 0.8.13 — unlock Destruction : tag + première Baguette de Destruction.
tag @s add capskills.magie.r1
execute unless entity @s[tag=capskills.item.baguette_destruction_given] run function capskills:give/mod/baguette_destruction_self
tag @s add capskills.item.baguette_destruction_given
