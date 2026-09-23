function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Les Bas-Anneaux tiennent parce que certains ferment les yeux, et parce que d’autres les gardent ouverts.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Ici, le calme n’est jamais gratuit. Il faut toujours vérifier qui l’a payé.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Si tu aides quelqu’un, fais-le proprement. Sinon je devrai écrire un rapport.","color":"white"}]
