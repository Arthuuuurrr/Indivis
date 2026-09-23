function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : La Capitale reçoit marchands, ouvriers, messagers et curieux. Elle s’en accommode mieux quand chacun laisse au moins un nom sur un registre.","color":"white"}]
scoreboard players set @s CAP_QSEQ 501
scoreboard players set @s CAP_QSEQ_TIMER 20
