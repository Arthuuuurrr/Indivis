function capitale:player/ensure_runtime_self
# Hotfix 15 : la criminalité passe avant le cooldown de dialogue normal.
scoreboard players add @s NPC_NEAR_CD 0
scoreboard players add @s CAP_NEAR_REACT 0
# Réaction criminelle immédiate, seulement limitée par CAP_NEAR_REACT dans les fonctions crime/*.
execute if score @s CAP_CRIME matches 10.. run function capitale:npc/gardeprofondeurs/static_port/crime/near_self
# Les phrases RP normales restent bridées par NPC_NEAR_CD.
execute unless score @s CAP_CRIME matches 10.. unless score @s NPC_NEAR_CD matches 1.. run function capitale:npc/gardeprofondeurs/static_port/dialogue/near/route
