scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Cette auberge tient les premiers couchages. Si vous comptez vraiment rester, louez une chambre ici, puis présentez-vous au magistrat des Profondeurs.","color":"white"}]
scoreboard players add @s REP_GARDEPROFONDEURS 5
function capitale:bounds/reputation_all_self
scoreboard players set @s QUEST_PROFONDEURS 100
scoreboard players set @s QUEST_PROLOGUE 50
scoreboard players set @s CAP_QUETEACTIVE 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Au seuil des Profondeurs","color":"white"}
function capitale:dialogue/sound/gain_quete_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Au seuil des Profondeurs. Réputation Gardes des Profondeurs +5.","color":"gold"}
function capitale:quest/au_seuil_des_profondeurs/escort_colin/return_start
