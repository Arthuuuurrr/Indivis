
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[Cet acte vous revient.]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set 220"},"hover_event":{"action":"show_text","value":"Restituer l’acte à Maître Lucain Perrin."}},{"text":"   ","color":"gray"},{"text":"[Je dois encore réfléchir.]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 221"},"hover_event":{"action":"show_text","value":"Conserver l’acte pour le moment."}}]
