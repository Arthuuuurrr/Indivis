# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Vous venez pour le registre ? Donnez donc la ligne fautive, que l’on tranche cela avant que deux services ne s’en mêlent.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 2 run function capitale:dialogue/random/roll_2_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 2 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 2 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Les quais ont leurs cris, les registres leurs ratures. Moi, j’essaie seulement que les deux racontent la même chose.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 2 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 2 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Si vous cherchez un navire, demandez au vent. Si vous cherchez une erreur, demandez aux bureaux.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
