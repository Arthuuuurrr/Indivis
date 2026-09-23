function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Alors je chercherai d’autres jambes. Les registres n’attendent guère, mais ils savent se venger par la paperasse.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Je ne vous retiens point. La Banque préfère un refus franc à une course commencée sans volonté.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Soit. Les comptes trouveront un autre porteur, ou davantage de retard.", "color": "white"}]
