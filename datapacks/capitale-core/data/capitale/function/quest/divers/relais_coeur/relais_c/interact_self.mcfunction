
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 20..49 if score @s CAP_RELAIS_COEUR_C matches 0 run function capitale:quest/divers/relais_coeur/relais_c/inspect_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 if score @s CAP_RELAIS_COEUR_C matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 if score @s CAP_RELAIS_COEUR_C matches 1.. run tellraw @s [{"text":"[Relais inférieur]","color":"yellow"},{"text":" : Ce relais a déjà été relevé. Cherchez les autres bornes ou retournez au Technicien.","color":"white"}]
execute unless score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_RELAIS_COEUR matches 20..50 run tellraw @s [{"text":"[Relais inférieur]","color":"yellow"},{"text":" : Vous ne disposez d’aucune instruction pour manipuler cette borne.","color":"white"}]
