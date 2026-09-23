function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Si vous venez d’arriver, adressez-vous à moi avant de quitter l’axe autorisé.","color":"white"}]
scoreboard players set @s CAP_QSEQ 732
scoreboard players set @s CAP_QSEQ_TIMER 20
