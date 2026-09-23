function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 24
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " La Banque demande qu’un pli scellé rejoigne son guichet avant la clôture du registre. Le cachet doit rester intact.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Un document de comptes doit gagner la Banque sans délai ni curiosité. S’il arrive fermé, vos pas seront payés.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Voici une commission de guichet : porter, non lire. La Banque récompense l’exactitude plus volontiers que les questions.", "color": "white"}]
function capitale:quest/journalieres/banque/employe/show_choices_self
