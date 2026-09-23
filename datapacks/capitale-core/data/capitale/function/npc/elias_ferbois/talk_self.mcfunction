function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Elias Ferbois]","color":"aqua"},{"text":" : Une forge ne ment pas. Le métal tient, ou il casse.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Elias Ferbois]","color":"aqua"},{"text":" : Les commandes montent vers les beaux quartiers. Les dettes, elles, restent ici.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Elias Ferbois]","color":"aqua"},{"text":" : Si tu cherches du travail honnête, commence par ne pas poser une caisse sur tes pieds.","color":"white"}]
