execute store result score @s CAP_REWARD_SUBROLL run random value 1..5
execute if score @s CAP_REWARD_SUBROLL matches 1 run function capitale:rewards/daily_bonus/items/common/coal_self
execute if score @s CAP_REWARD_SUBROLL matches 2 run function capitale:rewards/daily_bonus/items/common/copper_ingot_self
execute if score @s CAP_REWARD_SUBROLL matches 3 run function capitale:rewards/daily_bonus/items/common/torch_self
execute if score @s CAP_REWARD_SUBROLL matches 4 run function capitale:rewards/daily_bonus/items/common/paper_self
execute if score @s CAP_REWARD_SUBROLL matches 5 run function capitale:rewards/daily_bonus/items/common/bread_self
