function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : L’affaire est classée, et vous avez prouvé que vous compreniez la valeur de la retenue.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Vous avez su laisser l’ordre passer avant l’élan du moment. C’est une discipline que je remarque.","color":"white"}]
