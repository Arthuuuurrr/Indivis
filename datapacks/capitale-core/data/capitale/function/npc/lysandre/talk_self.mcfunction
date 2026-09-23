function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Dans ce marché, l’information arrive rarement seule. Elle voyage avec des dettes, des colis et des témoins gênants.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Regarde les mains avant d’écouter les promesses. C’est souvent là que la vérité se cache.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Les Bas-Anneaux parlent bas, mais ils parlent beaucoup.","color":"white"}]
