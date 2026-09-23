function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je vous attends. Dans le Cœur, mieux vaut suivre les voies ouvertes que les deviner.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je vous attends. Dans le Cœur, mieux vaut suivre les voies ouvertes que les deviner .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Écoutez bien. Je vous attends. Dans le Cœur, mieux vaut suivre les voies ouvertes que les deviner.","color":"white"}]
scoreboard players set @s CAP_ESCORT_WAIT_CD 100
