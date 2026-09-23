
function capitale:quest/dialogue/clear_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Très bien. Les quais sont tout proches. Restez un instant dans la cabine ; un dirigeable de la Couronne n’épouse jamais son appontage sans faire parler ses cloisons.","color":"white"}]
scoreboard players set @s CAP_QSEQ 301
scoreboard players set @s CAP_QSEQ_TIMER 20
