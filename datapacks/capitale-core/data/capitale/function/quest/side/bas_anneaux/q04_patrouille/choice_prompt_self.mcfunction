scoreboard players add @s QUEST_SIDE_BA_Q04 0
execute unless score @s QUEST_SIDE_BA_Q04 matches 50 run tellraw @s {"text":"[Quête] Aucun choix de patrouille n’est attendu maintenant.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run function capitale:quest/dialogue/clear_self
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run scoreboard players set @s CAP_QDIALOG_OWNER 23
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run scoreboard players set @s CAP_QDIALOG_KEEP 1
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run function capitale:quest/dialogue/anchor/create_self
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run scoreboard players enable @s QuestChoix
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Le voleur est là. Je te laisse choisir comment finir cette ronde.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run tellraw @s [{"text":"[Choix] ","color":"gold"},{"text":"[Arrêter strictement]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 233"}},{"text":"  ","color":"gray"},{"text":"[Avertir et laisser partir]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 234"}},{"text":"  ","color":"gray"},{"text":"[Restituer et signaler]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set 235"}}]
