function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q04 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute if score @s QUEST_SIDE_BA_Q04 matches 0 run function capitale:quest/side/bas_anneaux/q04_patrouille/offer_self
execute if score @s QUEST_SIDE_BA_Q04 matches 20 run function capitale:quest/side/bas_anneaux/q04_patrouille/remind_inspection_self
execute if score @s QUEST_SIDE_BA_Q04 matches 30 run function capitale:quest/side/bas_anneaux/q04_patrouille/remind_dispute_self
execute if score @s QUEST_SIDE_BA_Q04 matches 40 run function capitale:quest/side/bas_anneaux/q04_patrouille/remind_marchand_self
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run function capitale:quest/side/bas_anneaux/q04_patrouille/choice_prompt_self
execute if score @s QUEST_SIDE_BA_Q04 matches 101 run function capitale:quest/side/bas_anneaux/q04_patrouille/after_arrest_self
execute if score @s QUEST_SIDE_BA_Q04 matches 102 run function capitale:quest/side/bas_anneaux/q04_patrouille/after_warn_self
execute if score @s QUEST_SIDE_BA_Q04 matches 103 run function capitale:quest/side/bas_anneaux/q04_patrouille/after_balanced_self
