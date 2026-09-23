
execute if score @s CAP_PASS_ZONE matches 20 if score @s CAP_PASS_TIMER matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_PASS_ZONE matches 20 if score @s CAP_PASS_TIMER matches 1.. run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Votre passage vers les Quartiers hauts sud est encore valable. Ne traînez pas : les salons aiment la ponctualité quand elle vient des autres.","color":"white"}]
execute unless score @s CAP_PASS_ZONE matches 20 run function capitale:quest/divers/acte_dette/odon/renew_south_pass_commit_self
execute if score @s CAP_PASS_ZONE matches 20 unless score @s CAP_PASS_TIMER matches 1.. run function capitale:quest/divers/acte_dette/odon/renew_south_pass_commit_self
