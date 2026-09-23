function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/after_helped_self
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/after_denounced_self
execute unless score @s QUEST_SIDE_BA_Q01 matches 101..102 run function capitale:dialogue/sound/parole_simple_self
execute unless score @s QUEST_SIDE_BA_Q01 matches 101..102 run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Tu cherches quelque chose ? Dans les Bas-Anneaux, ça veut souvent dire que quelqu’un l’a déjà trouvé avant toi.","color":"white"}]
