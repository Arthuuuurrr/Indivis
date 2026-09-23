function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Je vous attends. Les registres ne courront pas jusqu’à nous.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Je vous attends. Les registres ne courront pas jusqu’à nous .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Écoutez bien. Je vous attends. Les registres ne courront pas jusqu’à nous.","color":"white"}]
scoreboard players set @s CAP_ESCORT_WAIT_CD 100
