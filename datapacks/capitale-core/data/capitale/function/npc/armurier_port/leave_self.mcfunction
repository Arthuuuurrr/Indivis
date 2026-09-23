function capitale:dialogue/random/roll_4_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Comme vous voudrez. Le métal ne s’impatiente pas ; les vivants, parfois.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Revenez si vos épaules réclament mieux qu’une chemise.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Gardez vos distances avec les ennuis. À défaut, gardez au moins une bonne cuirasse.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Les pièces seront encore là. Les occasions de s’en servir, elles, viennent sans prévenir.","color":"white"}]
