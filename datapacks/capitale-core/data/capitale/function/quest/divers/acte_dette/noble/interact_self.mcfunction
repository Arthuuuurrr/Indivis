
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:quest/divers/acte_dette/noble/attempt_complete_self
execute unless score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:quest/divers/acte_dette/noble/outside_quest_self
