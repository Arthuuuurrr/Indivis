function capitale:player/ensure_runtime_self
execute if score @s QUEST_RESIDENCE_PROF matches ..19 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_RESIDENCE_PROF matches ..19 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Une chambre ? Peut-être. Mais si vous cherchez un statut dans les registres, voyez d’abord le magistrat des Profondeurs.","color":"white"}]
execute if score @s QUEST_RESIDENCE_PROF matches 20 if score @s CAP_CHAMBRE_PROF matches 0 run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/open_choices_self
execute if score @s QUEST_RESIDENCE_PROF matches 20 if score @s CAP_CHAMBRE_PROF matches 1.. run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/already_room_self
execute if score @s QUEST_RESIDENCE_PROF matches 30.. if score @s CAP_CHAMBRE_PROF matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_RESIDENCE_PROF matches 30.. if score @s CAP_CHAMBRE_PROF matches 1.. run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Votre chambre est à vous pour la nuit. Pour le reste, retournez voir le magistrat.","color":"white"}]
