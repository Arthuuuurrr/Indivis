scoreboard players add @s CAPSK_REWARD_VN1 0
execute if score @s CAPSK_REWARD_VN1 matches 0 run function capskills:reward/core/vent_noir_q1_self
execute if score @s CAPSK_REWARD_VN1 matches 0 run function capitale:reward/xp_vanilla/core/vent_noir_q1_self
execute if score @s CAPSK_REWARD_VN1 matches 0 run scoreboard players set @s CAPSK_REWARD_VN1 1
