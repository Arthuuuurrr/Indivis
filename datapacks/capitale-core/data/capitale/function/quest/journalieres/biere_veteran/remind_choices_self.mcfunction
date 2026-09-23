scoreboard players set @s CAP_QDIALOG_OWNER 8
scoreboard players set @s CAP_QDIALOG_KEEP 1
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[J’ai la bière.]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 105"},"hover_event":{"action":"show_text","value":"Remettre la bière si elle est dans votre inventaire."}},{"text":"   ","color":"gray"},{"text":"[Pas encore.]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 106"},"hover_event":{"action":"show_text","value":"Revenir plus tard."}}]
