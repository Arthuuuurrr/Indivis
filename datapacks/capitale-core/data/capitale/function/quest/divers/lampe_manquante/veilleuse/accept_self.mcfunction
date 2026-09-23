function capitale:quest/journalieres/cooldown/sync_self
function capitale:player/ensure_runtime_self
scoreboard players add @s CAP_CD_GLOBE 0
execute if score @s CAP_CD_GLOBE matches 1.. run function capitale:quest/divers/lampe_manquante/veilleuse/postquest_self
execute unless score @s CAP_CD_GLOBE matches 1.. run function capitale:quest/divers/lampe_manquante/veilleuse/accept_commit_self
