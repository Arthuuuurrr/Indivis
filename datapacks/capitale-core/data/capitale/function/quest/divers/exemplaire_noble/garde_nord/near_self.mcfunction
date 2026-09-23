# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 1.. run tellraw @s [{"text":"[Garde du quartier nord]","color":"#FF8C00"},{"text":" : Laissez-passer du libraire reconnu. Le quartier vous est ouvert pour cette commission, pas pour y prendre vos aises.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 0 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 0 run tellraw @s [{"text":"[Garde du quartier nord]","color":"#FF8C00"},{"text":" : Le quartier nord n’accueille pas les flâneurs sans motif reconnu.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20..30 if score @s ACCESS_QH_NORD matches 1.. run tellraw @s [{"text":"[Garde du quartier nord]","color":"#FF8C00"},{"text":" : Votre accès est en règle. Circulez avec la retenue qu’exige ce quartier.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
