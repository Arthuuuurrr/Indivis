function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_GARDEPORT 20
scoreboard players set @s QUEST_PROLOGUE 20
scoreboard players set @s CAP_QUETEACTIVE 4
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Venez. Le magistrat siège à l’entrée des bureaux d’enregistrement du Port, près du comptoir administratif, sous la grande horloge des Archives.","color":"white"}]
scoreboard players set @s CAP_QSEQ 512
scoreboard players set @s CAP_QSEQ_TIMER 20
