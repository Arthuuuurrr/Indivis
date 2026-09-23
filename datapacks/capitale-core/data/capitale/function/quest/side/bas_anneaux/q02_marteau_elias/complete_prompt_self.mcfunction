function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 21
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/dialogue/anchor/create_self
function capitale:dialogue/sound/parole_quete_self
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Elias Ferbois]","color":"aqua"},{"text":" : La forge respire mieux. Viens, je vais te montrer ce que donne un travail propre.","color":"white"}]
tellraw @s [{"text":"[Choix] ","color":"gold"},{"text":"[Terminer la mission]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 213"}}]
