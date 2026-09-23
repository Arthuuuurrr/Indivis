
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous toucherons bientôt les quais. Posez vos questions à présent : je vous dirai ce que je sais, sans inventer ce qui m’échappe.","color":"white"}]
scoreboard players set @s CAP_QSEQ 203
scoreboard players set @s CAP_QSEQ_TIMER 20
