# RC9aj — remise à zéro complète du prologue pour test.
# À utiliser uniquement sur joueur de test.
scoreboard players set @s QUEST_SPAWN 0
scoreboard players set @s QUEST_PROLOGUE 0
scoreboard players set @s QUEST_GARDEPORT 0
scoreboard players set @s QUEST_GARDECOEUR 0
scoreboard players set @s QUEST_PROFONDEURS 0
scoreboard players set @s QUEST_RESIDENCE_PROF 0
scoreboard players set @s CAP_QDIALOG_OWNER 0
scoreboard players set @s CAP_QDIALOG_KEEP 0
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s QuestChoix 0
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_ESCORT_WAIT_CD 0
tellraw @s [{"text":"[Admin] Prologue remis à zéro pour test.","color":"gold"}]
