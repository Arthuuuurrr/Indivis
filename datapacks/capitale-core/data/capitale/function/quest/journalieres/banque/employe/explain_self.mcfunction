function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Des écritures de dépôt, des signatures et quelques chiffres dont la Banque garde jalousement le détail. Vous n’avez pas à les lire ; vous avez à les porter.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Un pli de registre, rien qui regarde les porteurs. Le Cercle marchand tient debout parce que chacun garde sa main à son office.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Le cachet protège moins le papier que l’ordre des comptes. Rompez-le, et le banquier ne verra plus qu’une faute.", "color": "white"}]
scoreboard players set @s CAP_QDIALOG_OWNER 24
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/journalieres/banque/employe/show_choices_self
