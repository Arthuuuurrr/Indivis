
function capitale:quest/dialogue/clear_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 1..2 run function capitale:quest/divers/relais_coeur/technicien/accept_commit_self
