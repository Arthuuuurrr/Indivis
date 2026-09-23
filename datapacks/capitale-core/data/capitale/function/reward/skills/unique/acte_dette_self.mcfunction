scoreboard players add @s CAPSK_REWARD_DETTE 0
execute if score @s CAPSK_REWARD_DETTE matches 0 run function capskills:reward/unique/minor_self
execute if score @s CAPSK_REWARD_DETTE matches 0 run function capitale:reward/xp_vanilla/unique/minor_self
execute if score @s CAPSK_REWARD_DETTE matches 0 run scoreboard players set @s CAPSK_REWARD_DETTE 1
