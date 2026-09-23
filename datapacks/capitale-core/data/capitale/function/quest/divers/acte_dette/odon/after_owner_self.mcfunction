function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Les scrupules coûtent cher. Pas toujours tout de suite, c’est ce qui les rend dangereux.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Les consciences sont de nobles conseillères. Elles ont seulement le défaut d’être coûteuses.","color":"white"}]
