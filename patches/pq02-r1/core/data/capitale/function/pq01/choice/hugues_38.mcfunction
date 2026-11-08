# PQ01-R3 — dialogues approuvés et scènes personnelles.
execute if score @s hcm_pq1_pick matches 1 run return run function capitale:pq01/node/hugues_36
execute if score @s hcm_pq1_pick matches 2 run return run function capitale:pq01/node/hugues_38
execute if score @s hcm_pq1_pick matches 3 run return run function capitale:pq01/node/hugues_40
execute if score @s hcm_pq1_pick matches 4 run return run function capitale:pq01/node/hugues_42
execute if score @s hcm_pq1_pick matches 5 run return run function capitale:pq01/node/hugues_44
execute if score @s hcm_pq1_pick matches 6 run return 0
execute if score @s hcm_pq1_pick matches 7 run return run function capitale:pq01/node/hugues_46
tellraw @s {"text":"Ce choix ne correspond plus à cette conversation.","color":"gray"}
