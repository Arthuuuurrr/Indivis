# RC9l lazy init QUEST : valeurs créées uniquement avant lecture.
scoreboard players add @s QUEST_DIVERS_LIBRAIRE_NOBLE 0
function capitale:quest/dialogue/clear_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 0 run function capitale:quest/divers/exemplaire_noble/libraire/offer_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run function capitale:quest/divers/exemplaire_noble/libraire/remind_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 run function capitale:quest/divers/exemplaire_noble/libraire/complete_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 100 run function capitale:quest/divers/exemplaire_noble/libraire/after_complete_self
