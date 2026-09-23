scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Un acte privé m’a échappé. Une dette, des signatures, rien qui concerne les bavards. Il serait regrettable qu’un papier aussi mal compris traîne dans les Profondeurs.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Il se trouve entre les mains d’un certain Maître Lucain Perrin, au Quartier des Vieilles Mécaniques, près du clocher. Ramenez-le-moi.","color":"white"}]
function capitale:quest/divers/acte_dette/sieur_odon/open_choices_self
