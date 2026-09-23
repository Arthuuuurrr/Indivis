function capitale:player/ensure_runtime_self
execute if score @s CAP_CD_BEER_GC matches 0 if score @s QUEST_DAILY_BEER_GC matches 100.. run scoreboard players set @s QUEST_DAILY_BEER_GC 0
execute if score @s CAP_CD_BEER_GC matches 1.. run function capitale:quest/journalieres/biere_veteran/cooldown_self
execute if score @s CAP_CD_BEER_GC matches 0 if score @s QUEST_DAILY_BEER_GC matches 0 run function capitale:quest/journalieres/biere_veteran/offer_self
execute if score @s CAP_CD_BEER_GC matches 0 if score @s QUEST_DAILY_BEER_GC matches 10 run function capitale:quest/journalieres/biere_veteran/remind_self
execute if score @s CAP_CD_BEER_GC matches 0 if score @s QUEST_DAILY_BEER_GC matches 100.. run function capitale:quest/journalieres/biere_veteran/story_offer_open_self
