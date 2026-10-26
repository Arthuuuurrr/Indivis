function hc_quetes:runtime/ensure_self
execute if score @s hcm_damage matches 1.. run scoreboard players set @s hcm_combat 200
execute if score @s hcm_attack matches 1.. run scoreboard players set @s hcm_combat 200
scoreboard players set @s hcm_damage 0
scoreboard players set @s hcm_attack 0
execute if score @s hcm_combat matches 1.. run scoreboard players remove @s hcm_combat 20
execute if score @s hcm_mess_cd matches 1.. run scoreboard players remove @s hcm_mess_cd 20
execute if score @s hcm_dlg_ttl matches 1.. run scoreboard players remove @s hcm_dlg_ttl 20
execute if score @s hcq_mqscene matches 1.. if score @s hcm_dlg_ttl matches ..0 run function hc_quetes:runtime/close_dialogue
execute if score @s hcq_mqscene matches 1.. if score @s CAP_CRIME matches 30.. run function hc_quetes:runtime/close_dialogue
execute if score @s hcq_mqscene matches 1.. unless function hc_quetes:runtime/session_near run function hc_quetes:runtime/close_dialogue
function hc_quetes:runtime/check_exit
function hc_quetes:migration/items/check_self
function hc_quetes:runtime/ensure_markers with storage hc_quetes:session current
execute if score @s hcq_mq04 matches 10 if function hc_quetes:runtime/at_port run scoreboard players set @s hcq_mq04_o1 1
execute if score @s hcq_mq01 matches 0 if score @s hcm_mess_cd matches ..0 if function hc_quetes:runtime/messenger/eligible run function hc_quetes:runtime/messenger/try_spawn
execute if score @s hcq_mq01 matches 1.. run function hc_quetes:runtime/messenger/remove with storage hc_quetes:session current

# QMARK1 : offres du core, personnage Nexus courant.
function capitale:quest/markers/tick_self with storage hc_quetes:session current
