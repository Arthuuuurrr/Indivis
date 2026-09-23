advancement revoke @s only capitale:aldren/kill_morgra
execute if score @s QUEST_ALDREN_ORCS matches 10 run scoreboard players set @s CAP_ALDREN_MORGRA 1
execute if score @s QUEST_ALDREN_ORCS matches 10 run tellraw @s [{"text":"[Quête] ","color":"gold"},{"text":"Morgra la Sanglante a été vaincue !","color":"aqua"}]
function capitale:quest/side/menace_orcs/check_complete_self
