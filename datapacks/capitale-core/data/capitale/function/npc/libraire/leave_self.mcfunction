function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les livres attendent mieux que les hommes. Revenez quand vous voudrez.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les livres attendent mieux que les hommes. Repassez quand vous voudrez.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les livres attendent mieux que les hommes. Revenez quand vous voudrez.","color":"white"}]
