function capitale:quest/journalieres/cooldown/sync_self
function capitale:player/ensure_runtime_self
scoreboard players add @s CAP_CD_GLOBE 0
execute if score @s QUEST_DIVERS_LAMPE matches 30.. run scoreboard players set @s QUEST_DIVERS_LAMPE 0
execute if score @s QUEST_DIVERS_LAMPE matches 10 run function capitale:quest/divers/lampe_manquante/veilleuse/remind_self
execute if score @s QUEST_DIVERS_LAMPE matches 20 run function capitale:quest/divers/lampe_manquante/veilleuse/complete_self
execute if score @s QUEST_DIVERS_LAMPE matches 0 if score @s CAP_CD_GLOBE matches 1.. run function capitale:quest/divers/lampe_manquante/veilleuse/postquest_self
execute if score @s QUEST_DIVERS_LAMPE matches 0 unless score @s CAP_CD_GLOBE matches 1.. run function capitale:quest/divers/lampe_manquante/veilleuse/offer_self
