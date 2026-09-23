function capitale:quest/journalieres/cooldown/sync_self

function capitale:player/ensure_runtime_self
function capitale:quest/journalieres/temoignages_quais/count_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIGNAGES_COUNT matches ..1 run function capitale:quest/journalieres/temoignages_quais/greffier/remind_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIGNAGES_COUNT matches 2 run function capitale:quest/journalieres/temoignages_quais/greffier/open_partial_report_choices_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIGNAGES_COUNT matches 3 run function capitale:quest/journalieres/temoignages_quais/greffier/complete_full_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run function capitale:quest/journalieres/temoignages_quais/greffier/already_done_cooldown_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 unless score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run function capitale:quest/journalieres/temoignages_quais/greffier/offer_self
scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
