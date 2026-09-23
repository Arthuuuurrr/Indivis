function capitale:quest/journalieres/cooldown/sync_self
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_BANQUE matches 10 run function capitale:quest/journalieres/banque/employe/remind_self
execute unless score @s QUEST_DAILY_BANQUE matches 10 if score @s CAP_CD_BANQUE matches 1.. run function capitale:quest/journalieres/banque/employe/already_done_today_self
execute unless score @s QUEST_DAILY_BANQUE matches 10 unless score @s CAP_CD_BANQUE matches 1.. run function capitale:quest/journalieres/banque/employe/offer_self
