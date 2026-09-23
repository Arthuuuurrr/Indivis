function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute if score @s QUEST_SIDE_BA_Q01 matches 0 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/ambient_before_self
execute if score @s QUEST_SIDE_BA_Q01 matches 20 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/remind_self
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/choice_prompt_self
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/after_helped_self
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/after_denounced_self
