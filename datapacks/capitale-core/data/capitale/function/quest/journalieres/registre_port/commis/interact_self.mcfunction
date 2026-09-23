function capitale:quest/journalieres/cooldown/sync_self

function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run function capitale:quest/journalieres/registre_port/commis/complete_success_self
execute if score @s QUEST_DAILY_REGISTRE_PORT matches 1 run function capitale:quest/journalieres/registre_port/commis/remind_self
execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 if score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:quest/journalieres/registre_port/commis/already_done_today_self
execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 unless score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:quest/journalieres/registre_port/commis/offer_self
