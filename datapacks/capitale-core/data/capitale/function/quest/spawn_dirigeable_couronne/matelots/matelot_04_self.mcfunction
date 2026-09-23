function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : À terre, ne vous étonnez pas si un garde du Port vous adresse la parole. Dans cette ville, un nouvel arrivant compte autant qu’une cargaison dans les registres.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Un nouvel arrivant attire les registres aussi sûrement qu’une caisse scellée. Ne soyez pas surpris si la Garde du Port vous arrête un instant.","color":"white"}]
