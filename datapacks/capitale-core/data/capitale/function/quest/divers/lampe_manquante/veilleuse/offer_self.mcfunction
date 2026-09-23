function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 25
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:dialogue/anchor/create_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Les huit lanternes du socle du Globe terrestre se désaccordent. Quand leur rythme se trouble, les repères d’en bas deviennent moins sûrs.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Le Globe marque les passages comme un vieux veilleur marque les heures. Ses huit lanternes pâlissent ; il faut les reprendre une à une.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Les Profondeurs supportent mal les lumières incohérentes. Recalibrez les huit lanternes du Globe avant que les passages ne deviennent trompeurs.", "color": "white"}]
function capitale:quest/divers/lampe_manquante/veilleuse/show_choices_self
