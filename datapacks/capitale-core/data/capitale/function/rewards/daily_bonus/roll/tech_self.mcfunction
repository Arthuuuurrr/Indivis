execute store result score @s CAP_REWARD_ROLL run random value 1..100
execute if score @s CAP_REWARD_ROLL matches 1..45 run function capitale:rewards/daily_bonus/pool/equipement_base_self
execute if score @s CAP_REWARD_ROLL matches 46..60 run function capitale:rewards/daily_bonus/pool/outils_simples_self
execute if score @s CAP_REWARD_ROLL matches 61..80 run function capitale:rewards/daily_bonus/pool/ressources_utiles_self
execute if score @s CAP_REWARD_ROLL matches 81..92 run function capitale:rewards/daily_bonus/pool/bonus_martins_self
execute if score @s CAP_REWARD_ROLL matches 93..100 run function capitale:rewards/daily_bonus/pool/ressources_rares_self
