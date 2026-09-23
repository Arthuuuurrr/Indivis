function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Le Globe ne sert pas seulement d’ornement. Ses lanternes aident les gens d’en bas à lire les passages, les hauteurs et les retours. Une lumière mal accordée suffit parfois à perdre un novice.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Quand un portail fatigue, les lampes pâlissent avant les murs. Ce n’est pas science d’Archives, seulement vieille prudence des Profondeurs.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Les Ingénieurs nomment cela mesure, charge et réponse. Nous autres disons plus simplement : quand le Globe tremble, on le remet droit.", "color": "white"}]
scoreboard players set @s CAP_QDIALOG_OWNER 25
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/divers/lampe_manquante/veilleuse/show_choices_self
