# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self

execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 0 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 0 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Les quais brassent des caisses, des promesses et des dettes. Les deux dernières sentent souvent plus mauvais.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 20 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Lucain Perrin. Quartier des Vieilles Mécaniques, près du clocher. Il sait très bien de quel papier il s’agit.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 30 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous avez vu Lucain ? Alors vous savez maintenant que chaque dette a deux récits. Approchez.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Le laissez-passer pour les Quartiers hauts sud ne vous rend pas noble. Il vous rend seulement attendu.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous avez choisi la voie qui laisse les portes ouvertes. Les gens disent beaucoup de mal de la prudence, puis viennent l’acheter.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Les scrupules coûtent cher. Pas toujours tout de suite, c’est ce qui les rend dangereux.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
