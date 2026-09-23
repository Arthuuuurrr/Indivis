function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DAILY_BEER_GC 100
scoreboard players set @s CAP_CD_BEER_GC 72000
scoreboard players add @s REP_GARDECOEUR 3
scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
scoreboard players set @s CAP_DAILY_BEER_GC_EVER 1
function capitale:dialogue/sound/gain_quete_self
tellraw @s [{"text":"[Quête] Terminée : Une bière pour la relève.","color":"gold"}]
tellraw @s [{"text":"[Réputation] ","color":"gold"},{"text":"Garde du Cœur +3","color":"white"}]
function capitale:reward/skills/first_daily/beer_self
function capitale:reward/skills/daily/small_self
function capitale:quest/journalieres/biere_veteran/story_offer_open_self
