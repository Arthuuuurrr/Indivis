scoreboard players set @s CAP_QDIALOG_OWNER 8
scoreboard players set @s CAP_QDIALOG_KEEP 1
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[J’écoute.]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 102"},"hover_event":{"action":"show_text","value":"Écouter le souvenir du vétéran."}},{"text":"   ","color":"gray"},{"text":"[Une autre fois.]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 103"},"hover_event":{"action":"show_text","value":"Décliner poliment."}}]
