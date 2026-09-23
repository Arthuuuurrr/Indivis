# Répliques d'ambiance — Libraire agréé. Ne renvoie plus de boutons chat.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_4_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Un livre public n’est pas toujours un livre innocent. Il dit surtout ce que la Couronne juge utile de laisser lire.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les meilleurs lecteurs ne cherchent pas seulement les réponses. Ils apprennent à repérer ce qui manque entre deux lignes.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les Archives sont au-dessus, au sens propre comme au sens politique. Ici, nous vendons ce qui peut descendre jusqu’au peuple.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les textes interdits ne se demandent pas à voix haute. C’est souvent la première erreur des curieux.","color":"white"}]
