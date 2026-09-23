
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 0 run function capitale:quest/divers/acte_dette/odon/offer_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 1..2 run function capitale:quest/divers/acte_dette/odon/offer_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 20 run function capitale:quest/divers/acte_dette/odon/remind_lucain_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 30 run function capitale:quest/divers/acte_dette/odon/after_lucain_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:quest/divers/acte_dette/odon/renew_south_pass_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 1 run function capitale:quest/divers/acte_dette/odon/after_noble_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 2 run function capitale:quest/divers/acte_dette/odon/after_owner_self
