execute store result score @s CAP_REWARD_SUBROLL run random value 1..10
execute if score @s CAP_REWARD_SUBROLL matches 1..5 run function capitale:rewards/daily_bonus/items/martins/bonus_5_self
execute if score @s CAP_REWARD_SUBROLL matches 6..8 run function capitale:rewards/daily_bonus/items/martins/bonus_10_self
execute if score @s CAP_REWARD_SUBROLL matches 9..10 run function capitale:rewards/daily_bonus/items/martins/bonus_20_self
