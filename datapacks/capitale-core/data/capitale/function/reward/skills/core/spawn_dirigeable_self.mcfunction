scoreboard players add @s CAPSK_REWARD_SPAWN 0
execute if score @s CAPSK_REWARD_SPAWN matches 0 run function capskills:reward/core/spawn_dirigeable_self
execute if score @s CAPSK_REWARD_SPAWN matches 0 run function capitale:reward/xp_vanilla/core/spawn_dirigeable_self
execute if score @s CAPSK_REWARD_SPAWN matches 0 run scoreboard players set @s CAPSK_REWARD_SPAWN 1
