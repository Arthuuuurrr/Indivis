# 0.9.38 — unlock Chaman : Totem de Protection I.
tag @s add capskills.chaman.totem.protection.r1
execute unless entity @s[tag=capskills.item.fetiche_ancetres_given] run function capskills:give/mod/fetiche_ancetres_self
tag @s add capskills.item.fetiche_ancetres_given
