scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q01 matches 30 run tellraw @s {"text":"[Quête] Aucun choix n’est attendu pour cette quête à cet instant.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run function capitale:quest/dialogue/clear_self
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run scoreboard players set @s CAP_QDIALOG_OWNER 20
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run scoreboard players set @s CAP_QDIALOG_KEEP 1
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run function capitale:quest/dialogue/anchor/create_self
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Je sais. Voler, c’est mal. Mais ceux qui disent ça ont rarement faim.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run scoreboard players enable @s QuestChoix
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run tellraw @s [{"text":"[Choix] ","color":"gold"},{"text":"[Couvrir Mira]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 203"}},{"text":"  ","color":"gray"},{"text":"[La dénoncer]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 204"}}]
