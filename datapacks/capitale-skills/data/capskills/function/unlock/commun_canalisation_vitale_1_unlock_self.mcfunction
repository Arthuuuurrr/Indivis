# 0.8.13 — unlock Canalisation vitale : tag + première Baguette de Soins.
tag @s add capskills.commun.selfheal
execute unless entity @s[tag=capskills.item.baguette_soins_given] run function capskills:give/mod/baguette_soins_self
tag @s add capskills.item.baguette_soins_given
