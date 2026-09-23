scoreboard players add @s CAPSK_REWARD_VN2 0
execute if score @s CAPSK_REWARD_VN2 matches 0 run function capskills:reward/core/vent_noir_q2_self
execute if score @s CAPSK_REWARD_VN2 matches 0 run function capitale:reward/xp_vanilla/core/vent_noir_q2_self
execute if score @s CAPSK_REWARD_VN2 matches 0 run scoreboard players set @s CAPSK_REWARD_VN2 1
