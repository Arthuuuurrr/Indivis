
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous avions ordre de déposer plusieurs canons dans une zone que les cartes remises à bord laissaient presque muette. Les caisses ont quitté la soute, les ordres ont été exécutés, puis nous avons repris le cap de la Capitale.","color":"white"}]
scoreboard players set @s CAP_QSEQ 221
scoreboard players set @s CAP_QSEQ_TIMER 20
