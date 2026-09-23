# Libraire agréé : vendeur + quête Divers « Un exemplaire à remettre ».
# Le commerce doit être ouvert par un bouton EasyNPC natif Open Trading Menu. La quête passe par work_offer_self.
function capitale:player/ensure_runtime_self
# RC9l lazy init QUEST : valeurs créées uniquement avant lecture.
scoreboard players add @s QUEST_DIVERS_LIBRAIRE_NOBLE 0
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 run function capitale:quest/divers/exemplaire_noble/libraire/complete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 run function capitale:dialogue/random/roll_3_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Les Archives conservent et contrôlent. Moi, je vends ce que la Couronne accepte de laisser circuler.","color":"white"}]
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Ouvrages approuvés, copies utiles, chroniques expurgées. Les fonds réservés, eux, ne descendent pas jusqu’à mon comptoir.","color":"white"}]
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Lisez ce qui circule librement ; pour le reste, il faut autorisation, sceau, ou témérité mal avisée.","color":"white"}]
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 100 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : Votre dernière commission fut menée avec plus de soin que certaines lectures. Les ouvrages vous restent ouverts.","color":"white"}]
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 30 if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run tellraw @s [{"text":"[Libraire agréé]","color":"aqua"},{"text":" : L’exemplaire pour Dame Yselle attend encore sa destination.","color":"white"}]
