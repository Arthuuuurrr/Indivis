
function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 23
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/dialogue/anchor/create_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Deux témoignages suffisent pour un rapport recevable. Vous pouvez conclure maintenant, ou chercher le dernier pour éclaircir davantage l’affaire.","color":"white"}]
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[Présenter le rapport maintenant.]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 260"},"hover_event":{"action":"show_text","value":"Rendre un rapport partiel."}},{"text":"   ","color":"gray"},{"text":"[Je vais interroger le dernier témoin.]","color":"aqua","click_event":{"action":"run_command","command":"/trigger QuestChoix set 261"},"hover_event":{"action":"show_text","value":"Poursuivre la collecte."}}]
