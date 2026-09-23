# RC9ak — near distance compatible si CAP_QUETEACTIVE est resté bloqué par une étape précédente.
function capitale:player/ensure_runtime_self
# RC9ag — near distance compatible avec l’état d’attente QUEST_RESIDENCE_PROF=10.
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 0 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 0 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Si vous cherchez une adresse stable dans les Profondeurs, venez me parler.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 0 run scoreboard players set @s NPC_NEAR_CD 100
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 10 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Si vous cherchez une adresse stable dans les Profondeurs, venez me parler.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 10 run scoreboard players set @s NPC_NEAR_CD 100
