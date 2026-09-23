scoreboard players set @s CAP_QDIALOG_OWNER 8
scoreboard players set @s CAP_QDIALOG_KEEP 1
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[Je vous rapporte une bière.]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 100"},"hover_event":{"action":"show_text","value":"Accepter la journalière."}},{"text":"   ","color":"gray"},{"text":"[Pas maintenant.]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 101"},"hover_event":{"action":"show_text","value":"Refuser pour l’instant."}}]
