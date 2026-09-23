function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q03 0
execute if score @s QUEST_SIDE_BA_Q03 matches 0 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/offer_self
execute if score @s QUEST_SIDE_BA_Q03 matches 20 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/remind_elias_self
execute if score @s QUEST_SIDE_BA_Q03 matches 30 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/remind_greffier_self
execute if score @s QUEST_SIDE_BA_Q03 matches 40 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/remind_final_delivery_self
execute if score @s QUEST_SIDE_BA_Q03 matches 50 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/complete_prompt_self
execute if score @s QUEST_SIDE_BA_Q03 matches 100 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/after_complete_self
