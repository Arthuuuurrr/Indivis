function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Le dernier ressort a déjà pris la route aujourd’hui. Revenez à la prochaine rotation de vingt heures.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Les Messageries ont déjà donné leur ressort du jour. Reprenez la demande après la prochaine rotation.","color":"white"}]
