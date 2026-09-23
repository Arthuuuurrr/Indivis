
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Sa cabine se trouve sous le poste du timonier, au gaillard d’arrière. Présentez-vous à lui directement ; sur ce pont, une convocation ne se transmet pas de loin.","color":"white"}]
scoreboard players set @s CAP_QSEQ 103
scoreboard players set @s CAP_QSEQ_TIMER 20
