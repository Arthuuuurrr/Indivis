scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : J’ai un papier à récupérer. Rien d’héroïque : une dette, une signature, un homme des Profondeurs qui s’imagine qu’un document devient à lui parce qu’il le garde sous son toit.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Maître Lucain Perrin. Quartier des Vieilles Mécaniques, près du clocher. Récupérez l’acte, revenez me voir, et vous serez payé correctement.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous pouvez refuser. Mais dans cette ville, on finit souvent par travailler pour quelqu’un qui connaît quelqu’un.","color":"white"}]
function capitale:quest/divers/acte_dette/odon/open_choices_self
