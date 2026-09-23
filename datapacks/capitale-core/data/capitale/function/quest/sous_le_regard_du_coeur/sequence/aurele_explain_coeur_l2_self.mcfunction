function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Les marchands gravitent autour ; les visiteurs traversent ; les gardes veillent. L’Empereur exige que ces voies restent nettes.","color":"white"}]
scoreboard players set @s CAP_QSEQ 712
scoreboard players set @s CAP_QSEQ_TIMER 20
