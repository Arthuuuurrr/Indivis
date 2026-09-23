function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " La Banque tient ses comptes, ses coffres et ses silences. Les trois se consultent rarement sans motif.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " Si vous venez pour un dépôt, adressez-vous au guichet. Si vous venez par curiosité, contentez-vous du sol de l’entrée.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Banquier]", "color": "yellow"}, {"text": " Le Cercle marchand parle haut. Ici, les chiffres parlent bas, et les sceaux parlent les premiers.", "color": "white"}]
