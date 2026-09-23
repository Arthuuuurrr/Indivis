
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Le Port paraît d’abord pratique. Regardez mieux : les Archives le dominent, les contrôles l’ordonnent, et l’on finit toujours par entendre parler du Cœur.","color":"white"}]
scoreboard players set @s CAP_QSEQ 232
scoreboard players set @s CAP_QSEQ_TIMER 20
