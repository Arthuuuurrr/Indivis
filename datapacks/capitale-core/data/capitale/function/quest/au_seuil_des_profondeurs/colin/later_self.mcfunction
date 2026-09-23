function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Prenez le temps de respirer. Quand vous voudrez un vrai toit plutôt qu’un plafond quelconque, revenez me voir.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Prenez le temps de respirer. Quand vous voudrez un vrai toit plutôt qu’un plafond quelconque, repassez me voir.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Écoutez bien. Prenez le temps de respirer. Quand vous voudrez un vrai toit plutôt qu’un plafond quelconque, revenez me voir.","color":"white"}]
