
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Vous êtes en règle. Cet ascenseur vous déposera au milieu du Cœur mécanique de la cité. Ne vous écartez pas des voies autorisées : les gardes du Cœur vous indiqueront la suite.","color":"white"}]
scoreboard players set @s CAP_QSEQ 601
scoreboard players set @s CAP_QSEQ_TIMER 20
