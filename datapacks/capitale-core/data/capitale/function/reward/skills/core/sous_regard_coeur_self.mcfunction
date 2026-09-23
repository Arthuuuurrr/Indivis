scoreboard players add @s CAPSK_REWARD_AURELE 0
execute if score @s CAPSK_REWARD_AURELE matches 0 run function capskills:reward/core/sous_regard_coeur_self
execute if score @s CAPSK_REWARD_AURELE matches 0 run function capitale:reward/xp_vanilla/core/sous_regard_coeur_self
execute if score @s CAPSK_REWARD_AURELE matches 0 run scoreboard players set @s CAPSK_REWARD_AURELE 1
