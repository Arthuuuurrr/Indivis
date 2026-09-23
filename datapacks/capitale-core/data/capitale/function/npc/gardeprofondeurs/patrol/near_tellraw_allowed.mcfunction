# Proximité : criminalité prioritaire, sinon une réplique RP normale.
# Correction beta 1.1.2 : les profils neutres / intermédiaires ne doivent pas rendre le garde silencieux.
function capitale:logic/classify/gardeprofondeurs_self
execute if score @s CAP_CRIME matches 10.. run function capitale:npc/gardeprofondeurs/patrol/crime/near_self
execute unless score @s CAP_CRIME matches 10.. run function capitale:npc/gardeprofondeurs/patrol/dialogue/near/route
