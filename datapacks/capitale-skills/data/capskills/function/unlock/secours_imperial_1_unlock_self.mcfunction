# 0.9.35 — Secours impérial I : tag fonctionnel + catalyseur.
tag @s add capskills.secours.r1
execute unless entity @s[tag=capskills.item.baguette_soins_given] run function capskills:give/mod/baguette_soins_self
tag @s add capskills.item.baguette_soins_given
