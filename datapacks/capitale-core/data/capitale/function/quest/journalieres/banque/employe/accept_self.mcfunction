function capitale:quest/journalieres/cooldown/sync_self
function capitale:quest/dialogue/clear_self
function capitale:quest/guidage/sync_self
execute if score @s CAP_GUIDE_LOCK matches 1.. run function capitale:quest/blocked_guide_self
execute unless score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_CD_BANQUE matches 1.. run function capitale:quest/journalieres/banque/employe/already_done_today_self
execute unless score @s CAP_GUIDE_LOCK matches 1.. unless score @s CAP_CD_BANQUE matches 1.. run function capitale:quest/journalieres/banque/employe/accept_commit_self
