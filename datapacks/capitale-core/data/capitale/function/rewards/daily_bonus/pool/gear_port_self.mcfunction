execute store result score @s CAP_REWARD_SUBROLL run random value 1..5
execute if score @s CAP_REWARD_SUBROLL matches 1 run function capitale:rewards/daily_bonus/items/gear/plastron_cuir_service_self
execute if score @s CAP_REWARD_SUBROLL matches 2 run function capitale:rewards/daily_bonus/items/gear/copper_sword_service_self
execute if score @s CAP_REWARD_SUBROLL matches 3 run function capitale:rewards/daily_bonus/items/gear/iron_sword_guet_self
execute if score @s CAP_REWARD_SUBROLL matches 4 run function capitale:rewards/daily_bonus/items/custom/chainmail_leggings_eye_copper_self
execute if score @s CAP_REWARD_SUBROLL matches 5 run function capitale:rewards/daily_bonus/items/gear/iron_leggings_coast_self
