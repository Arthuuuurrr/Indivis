# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Ce ressort vient du Port ? Approchez, l’horloge compte mieux que nous.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Le clocher sonne mieux quand personne ne touche à ce qu’il ne comprend pas.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
