function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Timonier de la Couronne]","color":"yellow"},{"text":" : Belle prise de quai, malgré le vent de travers. Pas un hauban qui crie, pas une hélice forcée : j’accepte ce résultat.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Timonier de la Couronne]","color":"yellow"},{"text":" : Le quai a bien pris, malgré la traversée. Aucun hauban rompu, aucune hélice malmenée : je dors tranquille.","color":"white"}]
