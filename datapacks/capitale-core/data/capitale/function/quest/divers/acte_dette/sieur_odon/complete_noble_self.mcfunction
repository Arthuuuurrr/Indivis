function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Ce n’est pas à moi que l’acte doit revenir désormais. Dame Éléonore de Vaudrec vous attend dans les Quartiers hauts sud.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : L’affaire vous dépasse désormais moins que vous ne le croyez : Dame Éléonore de Vaudrec vous attend dans les Hauts sud.","color":"white"}]
