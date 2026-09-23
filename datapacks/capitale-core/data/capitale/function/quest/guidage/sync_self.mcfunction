scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
execute if score @s QUEST_GARDEPORT matches 20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if score @s QUEST_GARDEPORT matches 20 run scoreboard players set @s CAP_GUIDE_ID 1
execute if score @s QUEST_GARDECOEUR matches 20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if score @s QUEST_GARDECOEUR matches 20 run scoreboard players set @s CAP_GUIDE_ID 2
execute if score @s QUEST_GARDECOEUR matches 40 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if score @s QUEST_GARDECOEUR matches 40 run scoreboard players set @s CAP_GUIDE_ID 2
execute if score @s QUEST_PROFONDEURS matches 20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if score @s QUEST_PROFONDEURS matches 20 run scoreboard players set @s CAP_GUIDE_ID 3
execute if score @s QUEST_PROFONDEURS matches 50 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if score @s QUEST_PROFONDEURS matches 50 run scoreboard players set @s CAP_GUIDE_ID 4
