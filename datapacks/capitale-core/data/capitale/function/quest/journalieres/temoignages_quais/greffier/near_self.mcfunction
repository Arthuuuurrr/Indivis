function capitale:quest/journalieres/cooldown/sync_self
# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run function capitale:quest/journalieres/temoignages_quais/count_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Témoignages recueillis :","color":"white"},{"score":{"name":"@s","objective":"CAP_TEMOIGNAGES_COUNT"},"color":"gold"},{"text":"/3. Deux suffisent pour un rapport ; trois valent mieux.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Les derniers litiges ont déjà reçu leur part de paroles. Revenez après la prochaine rotation.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 unless score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 unless score @s CAP_CD_TEMOIGNAGES_QUAIS matches 1.. run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Trois gens ont vu la même querelle et racontent déjà trois affaires différentes. J’aurais besoin d’oreilles patientes.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
