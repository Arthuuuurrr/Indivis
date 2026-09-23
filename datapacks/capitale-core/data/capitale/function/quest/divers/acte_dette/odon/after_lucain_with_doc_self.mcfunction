
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 40
scoreboard players set @s CAP_ACTE_SUD_PASS 1
scoreboard players set @s CAP_PASS_ZONE 20
scoreboard players set @s CAP_PASS_TIMER 12000
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Il vous a parlé des montants, n’est-ce pas ? Des chiffres ajoutés, des lignes gonflées, de la pauvre main prise dans un engrenage trop grand pour elle.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Peut-être. Mais Lucain a signé le premier papier, demandé des délais, profité de l’indulgence, puis crié au vol quand les intérêts ont commencé à ressembler à une dette réelle.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : La bénéficiaire finale vous recevra dans les Quartiers hauts sud : Dame Éléonore de Vaudrec. Dix minutes de passage. Donnez-lui l’acte si vous pensez que les signatures doivent encore signifier quelque chose.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Choisissez : remettre l’acte à Dame Éléonore de Vaudrec dans les Quartiers hauts sud, ou le restituer à Lucain.","color":"yellow"}
title @s times 5 60 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Choisissez le destinataire de l’acte.","color":"white"}
execute at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.55 0.95
