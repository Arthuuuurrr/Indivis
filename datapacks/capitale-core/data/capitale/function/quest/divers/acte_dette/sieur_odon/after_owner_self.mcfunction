function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Certains choisissent l’émotion contre l’ordre. Je saurai au moins quelle sorte de service ne plus vous demander.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Vous avez préféré ce qui vous semblait juste à ce qui m’était utile. Je retiendrai la nuance.","color":"white"}]
