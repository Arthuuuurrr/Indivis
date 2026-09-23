function capitale:quest/journalieres/cooldown/sync_self

function capitale:quest/dialogue/clear_self
execute if score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:quest/journalieres/registre_port/commis/already_done_today_self
execute unless score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:quest/journalieres/registre_port/commis/accept_commit_self
