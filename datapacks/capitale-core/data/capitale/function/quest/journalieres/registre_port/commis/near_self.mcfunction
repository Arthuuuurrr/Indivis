function capitale:quest/journalieres/cooldown/sync_self
# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 1 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : L’Agent de quai attend encore votre passage. Un registre juste évite souvent une querelle inutile.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Vous avez la confirmation ? Revenez au comptoir, que je referme ce dossier.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 if score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 if score @s CAP_CD_REGISTRE_PORT matches 1.. run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Les registres ont déjà assez circulé. Revenez après la prochaine rotation de vingt heures.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 unless score @s CAP_CD_REGISTRE_PORT matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 unless score @s CAP_CD_REGISTRE_PORT matches 1.. run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Vous tombez à point. J’aurais justement besoin de jambes fiables pour un registre qui s’est égaré.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
