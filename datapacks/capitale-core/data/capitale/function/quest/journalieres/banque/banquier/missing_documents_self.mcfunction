function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " Vous me parlez d’un pli, mais vos mains n’en portent aucun. La Banque ne crédite pas les intentions.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " Sans document scellé, je n’inscris rien. Les comptes n’avancent pas sur parole seule.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " Revenez avec le pli attendu. Un guichet vide ne ferme aucune ligne de registre.", "color": "white"}]
