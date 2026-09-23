function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"──────── Règles des profils ────────","color":"gold","bold":true}
tellraw @s {"text":"— Changement autorisé","color":"yellow"}
tellraw @s {"text":"Le changement de personnage devra se faire dans un lieu sûr : Bureau des arrivées, auberge, greffe impérial ou fief autorisé.","color":"gray"}
tellraw @s {"text":"— Sécurité","color":"yellow"}
tellraw @s {"text":"Le futur mod devra sauvegarder l’état actuel, vider/charger l’état choisi, puis synchroniser le core et CapSkills.","color":"gray"}
tellraw @s {"text":"— Restrictions prévues","color":"yellow"}
tellraw @s {"text":"Pas de changement en combat, en transaction, en escorte critique, en prison, pendant une sanction, pendant un changement de dimension ou hors zone sûre.","color":"gray"}
tellraw @s {"text":"— Économie","color":"yellow"}
tellraw @s {"text":"Les profils doivent éviter toute duplication : inventaire, ender chest, monnaie physique, objets de quête, kits et récompenses doivent être sauvegardés/restaurés en bloc.","color":"gray"}
tellraw @s [{"text":"[","color":"dark_gray"},{"text":"Retour profils","color":"gray","click_event":{"action":"run_command","command":"/trigger InfoRP set 20"}},{"text":"]","color":"dark_gray"}]
