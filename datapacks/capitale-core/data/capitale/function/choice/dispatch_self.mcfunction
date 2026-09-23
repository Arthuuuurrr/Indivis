function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_CTX_TYPE matches 1 if score @s CapChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 1 if score @s CapChoix matches 1 run function capitale:choice/access/follow_self
execute if score @s CAP_CTX_TYPE matches 1 if score @s CapChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 1 if score @s CapChoix matches 2 run function capitale:choice/access/bribe_self
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 1 run function capitale:justice/intercept/follow_to_tribunal_self
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 2 run function capitale:justice/intercept/flee_30_49_self
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 3 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 2 if score @s CapChoix matches 3 run function capitale:justice/arrangement/attempt_self
execute if score @s CAP_CTX_TYPE matches 3 if score @s CapChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 3 if score @s CapChoix matches 1 run function capitale:justice/intercept/follow_to_tribunal_self
execute if score @s CAP_CTX_TYPE matches 3 if score @s CapChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 3 if score @s CapChoix matches 2 run function capitale:justice/intercept/flee_30_49_self
execute if score @s CAP_CTX_TYPE matches 4 if score @s CapChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 4 if score @s CapChoix matches 1 run function capitale:justice/intercept/follow_to_prison_self
execute if score @s CAP_CTX_TYPE matches 4 if score @s CapChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_CTX_TYPE matches 4 if score @s CapChoix matches 2 run function capitale:justice/intercept/flee_enemy_self
execute unless score @s CAP_FLAG matches 1 run function capitale:choice/invalid_self
# Réactivation locale du trigger après traitement du choix.
scoreboard players set @s CapChoix 0
scoreboard players enable @s CapChoix
