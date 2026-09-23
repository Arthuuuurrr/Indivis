
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Sur un relief battu par les vents, loin des mouillages et des routes marquées sur nos cartes. Vous respiriez encore, mais pas assez fort pour nous raconter votre histoire.","color":"white"}]
scoreboard players set @s CAP_QSEQ 211
scoreboard players set @s CAP_QSEQ_TIMER 20
