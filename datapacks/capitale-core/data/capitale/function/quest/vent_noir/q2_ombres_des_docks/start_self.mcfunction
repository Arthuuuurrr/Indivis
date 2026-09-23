function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_CAPITAINE matches 100 if score @s QUEST_VN_DOCKS matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q2_ombres_des_docks/start_commit_self
