# 0.8.13 — unlock Altération : tag + première Baguette d’Altération.
tag @s add capskills.alchimie.r1
execute unless entity @s[tag=capskills.item.baguette_alteration_given] run function capskills:give/mod/baguette_alteration_self
tag @s add capskills.item.baguette_alteration_given
