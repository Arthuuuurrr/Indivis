function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Je vous attends. Dans les Profondeurs, mieux vaut garder le fil que chercher le chemin après coup.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Je vous attends. Dans les Profondeurs, mieux vaut garder le fil que chercher le chemin après coup .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Écoutez bien. Je vous attends. Dans les Profondeurs, mieux vaut garder le fil que chercher le chemin après coup.","color":"white"}]
scoreboard players set @s CAP_ESCORT_WAIT_CD 100
