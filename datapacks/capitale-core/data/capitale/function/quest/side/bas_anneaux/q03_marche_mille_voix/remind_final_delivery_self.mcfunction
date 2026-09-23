scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Dernier paquet pour Mira. Elle se méfie des adultes, mais elle se méfie un peu moins de toi.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Porter le paquet à Mira.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Vu ton histoire avec Mira, mieux vaut remettre cette note à Roland. Les chemins changent selon les choix.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Remettre la note à Roland.","color":"white"}]
execute unless score @s QUEST_SIDE_BA_Q01 matches 101..102 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Dernière course : une enveloppe pour une habitante du vieux passage. Ne la perds pas.","color":"white"}]
execute unless score @s QUEST_SIDE_BA_Q01 matches 101..102 run tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Remettre l’enveloppe au vieux passage.","color":"white"}]
