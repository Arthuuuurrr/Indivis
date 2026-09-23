function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_GARDECOEUR 20
scoreboard players set @s QUEST_PROLOGUE 30
scoreboard players set @s CAP_QUETEACTIVE 3
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Suivez-moi. Je vous montre d’abord le Cercle marchand ; après cela, vous me retrouverez à l’ascenseur pour descendre vers les Profondeurs.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Sous le regard du Cœur — Suivez Aurèle jusqu’au Cercle marchand.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Sous le regard du Cœur","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Aurèle jusqu’au Cercle marchand.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/sous_le_regard_du_coeur/escort/start_to_commerces_self
