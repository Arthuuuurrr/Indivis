function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Restez dans mon sillage. Je vous conduis à l’auberge, pas à un pari de ruelles.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Restez dans mon sillage. Je vous conduis à l’auberge, pas à un pari de ruelles .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Écoutez bien. Restez dans mon sillage. Je vous conduis à l’auberge, pas à un pari de ruelles.","color":"white"}]
scoreboard players set @s CAP_ESCORT_WAIT_CD 100
