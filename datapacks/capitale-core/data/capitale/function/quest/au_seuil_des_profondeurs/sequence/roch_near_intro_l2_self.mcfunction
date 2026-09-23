function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Approchez. Je peux vous mener jusqu’au poste inférieur ; là-bas, on vous orientera mieux que je ne le ferai depuis cet ascenseur.","color":"white"}]
scoreboard players set @s CAP_QSEQ 832
scoreboard players set @s CAP_QSEQ_TIMER 20
