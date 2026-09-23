execute store result score @s CAP_REWARD_SUBROLL run random value 1..4
execute if score @s CAP_REWARD_SUBROLL matches 1 run function capitale:rewards/daily_bonus/items/custom/copper_helmet_raiser_netherite_self
execute if score @s CAP_REWARD_SUBROLL matches 2 run function capitale:rewards/daily_bonus/items/custom/copper_boots_snout_netherite_self
execute if score @s CAP_REWARD_SUBROLL matches 3 run function capitale:rewards/daily_bonus/items/custom/copper_chestplate_coast_netherite_self
execute if score @s CAP_REWARD_SUBROLL matches 4 run function capitale:rewards/daily_bonus/items/custom/chainmail_leggings_eye_copper_self
