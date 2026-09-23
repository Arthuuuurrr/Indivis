
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Je ne vous prêterai donc ni passé ni intention. Ce récit vous appartient.","color":"white"}]
scoreboard players set @s CAP_QSEQ 212
scoreboard players set @s CAP_QSEQ_TIMER 20
