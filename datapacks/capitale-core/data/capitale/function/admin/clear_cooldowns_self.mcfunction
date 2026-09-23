function capitale:player/ensure_runtime_self
scoreboard players set @s NPC_NEAR_CD 0
scoreboard players set @s CAP_ARREST_CD 0
scoreboard players set @s CAP_ASSAULT_CD 0
scoreboard players set @s CAP_ARREST_PENDING 0
scoreboard players set @s CAP_ARREST_TIMER 0
scoreboard players set @s CAP_ACCESS_CD 0
scoreboard players set @s CAP_BRIBE_CD 0
scoreboard players set @s CAP_ACCESS_WARN 0
scoreboard players set @s CapChoix 0
scoreboard players set @s CAP_CTX_TIMER 0
scoreboard players set @s CAP_CTX_MODE 0
scoreboard players set @s CAP_CTX_FACTION 0
scoreboard players set @s CAP_CTX_ZONE 0
scoreboard players set @s CAP_CTX_TYPE 0
scoreboard players set @s CAP_NEAR_REACT 0
scoreboard players set @s CAP_TRIB_EXIT_MSG_CD 0
tellraw @s {"text":"[Admin] Cooldowns RP réinitialisés.","color":"gray"}
scoreboard players enable @s QuestChoix
scoreboard players enable @s ShopChoix
scoreboard players enable @s CapChoix
