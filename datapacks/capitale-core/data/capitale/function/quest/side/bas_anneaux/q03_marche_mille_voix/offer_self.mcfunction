function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 22
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/dialogue/anchor/create_self
function capitale:dialogue/sound/parole_quete_self
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Tu marches vite ? Tant mieux. Dans ce marché, les informations voyagent plus vite que les chevaux.","color":"white"}]
tellraw @s [{"text":"[Quête]","color":"gold"},{"text":" Secondaire Bas-Anneaux Q03 — Le Marché des Mille Voix","color":"white"}]
tellraw @s [{"text":"[Choix] ","color":"gold"},{"text":"[Accepter les livraisons]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 220"}},{"text":"  ","color":"gray"},{"text":"[Demander des précisions]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set 221"}},{"text":"  ","color":"gray"},{"text":"[Pas maintenant]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 222"}}]
