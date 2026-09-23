function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Sans inscription, les portes restent ouvertes, mais les regards se ferment plus vite. Si vous le souhaitez, je vous accompagne.","color":"white"}]
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
scoreboard players set @s CAP_QDIALOG_OWNER 2
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/le_registre_du_port/leovic/show_choices_self
