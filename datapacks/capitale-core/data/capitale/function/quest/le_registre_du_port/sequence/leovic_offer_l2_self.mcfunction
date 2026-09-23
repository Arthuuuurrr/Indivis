function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Dialogue de quête]","color":"light_purple"},{"text":" Léovic vous adresse une proposition.","color":"gray"}]
function capitale:quest/le_registre_du_port/leovic/open_choices_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
