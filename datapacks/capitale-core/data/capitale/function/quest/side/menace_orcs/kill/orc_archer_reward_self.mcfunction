advancement revoke @s only capitale:aldren/kill_orc_archer
execute if score @s QUEST_ALDREN_ORCS matches 10 if score @s CAP_ALDREN_ARCH matches ..5 run scoreboard players add @s CAP_ALDREN_ARCH 1
execute if score @s QUEST_ALDREN_ORCS matches 10 run tellraw @s [{"text":"[Quête] ","color":"gold"},{"text":"Archer Orc éliminé : ","color":"white"},{"score":{"name":"@s","objective":"CAP_ALDREN_ARCH"},"color":"aqua"},{"text":" / 6","color":"white"}]
function capitale:quest/side/menace_orcs/check_complete_self
