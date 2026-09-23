function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : La passerelle est posée. Si vous cherchez encore la marche à suivre, Géraud Rivet reste sur le pont.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : La passerelle est libre. Si l’ordre de marche vous échappe encore, Géraud Rivet n’a pas quitté le pont.","color":"white"}]
