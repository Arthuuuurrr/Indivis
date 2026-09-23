function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Si vous cherchez un point d’appui, je peux vous mener au poste inférieur. De là, Colin vous indiquera l’auberge.","color":"white"}]
function capitale:quest/au_seuil_des_profondeurs/roch/open_choices_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
