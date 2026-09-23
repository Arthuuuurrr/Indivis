
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Sur un bâtiment de la Couronne, les raisons ne descendent pas toujours jusqu’aux mains qui tiennent le gouvernail.","color":"white"}]
scoreboard players set @s CAP_QSEQ 222
scoreboard players set @s CAP_QSEQ_TIMER 20
