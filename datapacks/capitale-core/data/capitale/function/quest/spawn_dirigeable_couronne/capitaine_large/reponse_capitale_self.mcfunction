
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : La Haute Capitale est le centre de la Couronne : ses quais accueillent, ses bureaux enregistrent, ses hauteurs décident. Un nouvel arrivant y rencontre souvent un registre avant de trouver sa première rue.","color":"white"}]
scoreboard players set @s CAP_QSEQ 231
scoreboard players set @s CAP_QSEQ_TIMER 20
