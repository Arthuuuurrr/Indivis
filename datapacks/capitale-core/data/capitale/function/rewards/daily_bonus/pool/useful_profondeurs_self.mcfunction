execute store result score @s CAP_REWARD_SUBROLL run random value 1..5
execute if score @s CAP_REWARD_SUBROLL matches 1 run function capitale:rewards/daily_bonus/items/useful/lantern_self
execute if score @s CAP_REWARD_SUBROLL matches 2 run function capitale:rewards/daily_bonus/items/useful/redstone_self
execute if score @s CAP_REWARD_SUBROLL matches 3 run function capitale:rewards/daily_bonus/items/useful/iron_ingot_self
execute if score @s CAP_REWARD_SUBROLL matches 4 run function capitale:rewards/daily_bonus/items/useful/xp_bottle_self
execute if score @s CAP_REWARD_SUBROLL matches 5 run function capitale:rewards/daily_bonus/items/useful/arrow_self
