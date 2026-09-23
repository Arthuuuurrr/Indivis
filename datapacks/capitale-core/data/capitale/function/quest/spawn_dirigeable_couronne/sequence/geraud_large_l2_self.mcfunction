
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Nous approchons de la Haute Capitale. Le capitaine Brumeforge souhaite vous parler avant que le bâtiment ne prenne le quai.","color":"white"}]
scoreboard players set @s CAP_QSEQ 102
scoreboard players set @s CAP_QSEQ_TIMER 20
