function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Faites donc. Une décision pressée fait souvent l’affaire de celui qui a falsifié la balance.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Prenez le temps qu’il faut. Un acte se lit deux fois quand il engage plus que de l’encre.","color":"white"}]
