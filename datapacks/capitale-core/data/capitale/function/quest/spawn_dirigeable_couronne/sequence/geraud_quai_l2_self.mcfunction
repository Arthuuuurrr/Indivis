
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Ne soyez pas surpris si un garde du Port vient à votre rencontre. Ici, l’accueil commence souvent par une indication, et se poursuit parfois devant un registre.","color":"white"}]
scoreboard players set @s CAP_QSEQ 402
scoreboard players set @s CAP_QSEQ_TIMER 20
