function capitale:quest/journalieres/cooldown/sync_self
# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 20 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Le ressort ne gagnera pas le clocher en restant dans votre sac. Filez vers les Vieilles Mécaniques.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Vous avez atteint le bon quartier. L’Horlogère du Clocher attend désormais la pièce.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_CD_RESSORT_CLOCHER matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_CD_RESSORT_CLOCHER matches 1.. run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Les ateliers ont reçu leur dernière urgence. Revenez après la prochaine rotation de service.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 unless score @s CAP_CD_RESSORT_CLOCHER matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 unless score @s CAP_CD_RESSORT_CLOCHER matches 1.. run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : J’ai une course qui n’aime pas attendre : un ressort calibré pour le clocher des Vieilles Mécaniques.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
