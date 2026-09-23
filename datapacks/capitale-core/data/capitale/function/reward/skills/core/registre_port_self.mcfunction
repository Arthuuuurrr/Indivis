scoreboard players add @s CAPSK_REWARD_REGPORT 0
execute if score @s CAPSK_REWARD_REGPORT matches 0 run function capskills:reward/core/registre_port_self
execute if score @s CAPSK_REWARD_REGPORT matches 0 run function capitale:reward/xp_vanilla/core/registre_port_self
execute if score @s CAPSK_REWARD_REGPORT matches 0 run scoreboard players set @s CAPSK_REWARD_REGPORT 1
