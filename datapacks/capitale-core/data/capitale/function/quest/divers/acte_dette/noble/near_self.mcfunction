# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Odon m’a prévenue de votre passage. Le papier que vous portez concerne une affaire trop ancienne pour être traitée dans un couloir.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute unless score @s QUEST_DIVERS_ACTE_DETTE matches 40 run tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Les Quartiers hauts sud reçoivent rarement les visites sans motif. Lorsque cela arrive, elles sont brèves.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
