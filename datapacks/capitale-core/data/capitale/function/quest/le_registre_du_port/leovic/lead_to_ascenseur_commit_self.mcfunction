
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Voilà qui est fait. Vous êtes désormais inscrit ; la Capitale saura au moins quel nom placer sur ses registres.","color":"white"}]
scoreboard players set @s CAP_QSEQ 611
scoreboard players set @s CAP_QSEQ_TIMER 20
