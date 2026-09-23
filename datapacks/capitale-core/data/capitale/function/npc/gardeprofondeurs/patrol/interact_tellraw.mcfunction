function capitale:player/ensure_runtime_self
function capitale:logic/classify/gardeprofondeurs_self
execute if score @s CAP_CRIME matches 10.. run function capitale:npc/gardeprofondeurs/patrol/crime/interaction_self
execute unless score @s CAP_CRIME matches 10.. run function capitale:npc/gardeprofondeurs/patrol/dialogue/interaction/route
