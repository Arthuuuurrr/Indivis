
function capitale:quest/dialogue/clear_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 1..2 run function capitale:quest/divers/acte_dette/sieur_odon/accept_commit_self
