# 0.9.39 — unlock Chaman : Totem de Guérison I.
tag @s add capskills.chaman.totem.guerison.r1
execute unless entity @s[tag=capskills.item.fetiche_ancetres_given] run function capskills:give/mod/fetiche_ancetres_self
tag @s add capskills.item.fetiche_ancetres_given
