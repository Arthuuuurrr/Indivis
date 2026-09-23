function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Le Cœur ne fabrique pas les plans. Il règle leurs distances, contient les brèches et garde Telluris d’un mélange trop violent avec Infernum, Exillium ou d’autres seuils.","color":"white"}]
scoreboard players set @s CAP_QDIALOG_OWNER 3
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/sous_le_regard_du_coeur/aurele/show_choices_self
