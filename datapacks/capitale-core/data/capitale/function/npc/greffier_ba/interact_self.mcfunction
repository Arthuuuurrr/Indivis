function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q03 0
execute if score @s QUEST_SIDE_BA_Q03 matches 30 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_greffier_npc_self
execute unless score @s QUEST_SIDE_BA_Q03 matches 30 run tellraw @s [{"text":"[Greffier]","color":"aqua"},{"text":" : Déposez vos demandes au bon guichet, et seulement si elles existent déjà sur un registre.","color":"white"}]
