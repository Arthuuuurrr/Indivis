function capitale:quest/journalieres/cooldown/sync_self

function capitale:player/ensure_runtime_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 20..49 run function capitale:quest/divers/relais_coeur/technicien/remind_or_renew_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 50 run function capitale:quest/divers/relais_coeur/technicien/complete_self
execute unless score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 if score @s CAP_CD_RELAIS_COEUR matches 1.. run function capitale:quest/divers/relais_coeur/technicien/already_done_cooldown_self
execute unless score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 unless score @s CAP_CD_RELAIS_COEUR matches 1.. run function capitale:quest/divers/relais_coeur/technicien/offer_self
