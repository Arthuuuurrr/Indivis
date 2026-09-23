function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Un acte de dette dort au mauvais endroit, et certaines mains seraient trop heureuses de l’oublier. Rapportez-le-moi, et chacun retrouvera la place que les chiffres lui donnent.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Il ne s’agit que d’un papier, dira-t-on. Mais dans cette ville, un papier bien signé peut peser plus lourd qu’une porte ferrée.","color":"white"}]
function capitale:quest/divers/acte_dette/odon/show_choices_self
