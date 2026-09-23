function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Ce sont les étages que les puissants regardent rarement longtemps. On y vit plus serré, on y paie moins cher, et l’on apprend vite à se rendre utile.","color":"white"}]
scoreboard players set @s CAP_QDIALOG_OWNER 4
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/au_seuil_des_profondeurs/roch/show_choices_self
