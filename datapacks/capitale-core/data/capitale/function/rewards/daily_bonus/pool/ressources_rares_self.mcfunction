execute store result score @s CAP_REWARD_SUBROLL run random value 1..5
execute if score @s CAP_REWARD_SUBROLL matches 1 run function capitale:rewards/daily_bonus/items/rare/diamond_self
execute if score @s CAP_REWARD_SUBROLL matches 2 run function capitale:rewards/daily_bonus/items/rare/gold_ingot_self
execute if score @s CAP_REWARD_SUBROLL matches 3 run function capitale:rewards/daily_bonus/items/rare/emerald_self
execute if score @s CAP_REWARD_SUBROLL matches 4 run function capitale:rewards/daily_bonus/items/rare/xp_bottle_self
execute if score @s CAP_REWARD_SUBROLL matches 5 run function capitale:rewards/daily_bonus/items/rare/lapis_block_self
