advancement revoke @s only capitale:aldren/kill_orc_champion
execute if score @s QUEST_ALDREN_ORCS matches 10 if score @s CAP_ALDREN_CHAMP matches ..2 run scoreboard players add @s CAP_ALDREN_CHAMP 1
execute if score @s QUEST_ALDREN_ORCS matches 10 run tellraw @s [{"text":"[Quête] ","color":"gold"},{"text":"Croc de Guerre éliminé : ","color":"white"},{"score":{"name":"@s","objective":"CAP_ALDREN_CHAMP"},"color":"aqua"},{"text":" / 3","color":"white"}]
function capitale:quest/side/menace_orcs/check_complete_self
