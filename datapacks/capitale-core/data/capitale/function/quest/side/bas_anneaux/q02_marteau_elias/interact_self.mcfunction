function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q02 0
execute if score @s QUEST_SIDE_BA_Q02 matches 0 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/offer_self
execute if score @s QUEST_SIDE_BA_Q02 matches 20 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/remind_caisse_self
execute if score @s QUEST_SIDE_BA_Q02 matches 30 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/remind_charbon_self
execute if score @s QUEST_SIDE_BA_Q02 matches 40 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/complete_prompt_self
execute if score @s QUEST_SIDE_BA_Q02 matches 100 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/after_complete_self
