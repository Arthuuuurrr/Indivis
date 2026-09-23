function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Nous sommes à quai. La Haute Capitale est devant vous ; la passerelle est libre.","color":"white"}]
scoreboard players set @s CAP_QSEQ 403
scoreboard players set @s CAP_QSEQ_TIMER 20
