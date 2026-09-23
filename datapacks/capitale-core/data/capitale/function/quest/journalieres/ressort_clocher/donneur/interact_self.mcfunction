function capitale:quest/journalieres/cooldown/sync_self

function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 run function capitale:quest/journalieres/ressort_clocher/donneur/remind_self
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_CD_RESSORT_CLOCHER matches 1.. run function capitale:quest/journalieres/ressort_clocher/donneur/already_done_cooldown_self
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 unless score @s CAP_CD_RESSORT_CLOCHER matches 1.. run function capitale:quest/journalieres/ressort_clocher/donneur/offer_self
