function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_ASCENSION matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 20 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Voilà notre monture. Ce transport n’a pas l’orgueil d’un corsaire, mais il sait encore grimper.","color":"white"}]
execute if score @s QUEST_VN_ASCENSION matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 20 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Chargez les caisses utiles. Ensuite, vérifiez les moteurs. Je préfère perdre du temps que tomber droit.","color":"white"}]
execute if score @s QUEST_VN_ASCENSION matches 20 run scoreboard players set @s QUEST_VN_ASCENSION 30
execute if score @s QUEST_VN_ASCENSION matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 30 run tellraw @s {"text":"[Objectif mis à jour] Chargez les caisses prévues pour l’ascension.","color":"yellow"}
execute if score @s QUEST_VN_ASCENSION matches 30 run title @s times 5 50 15
execute if score @s QUEST_VN_ASCENSION matches 30 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_ASCENSION matches 30 run title @s subtitle {"text":"Chargez les caisses utiles.","color":"white"}
execute if score @s QUEST_VN_ASCENSION matches 30 at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15
execute if score @s QUEST_VN_ASCENSION matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 40 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Les caisses sont à bord. Les moteurs, maintenant.","color":"white"}]
execute if score @s QUEST_VN_ASCENSION matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 50 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Tout est prêt. Donnez le signal quand vous voulez monter.","color":"white"}]
execute if score @s QUEST_VN_ASCENSION matches 60 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 60 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Tenez bon. Le Vent Noir ne nous attendra pas à hauteur d’homme.","color":"white"}]
execute if score @s QUEST_VN_ASCENSION matches 100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Le pont ennemi est devant nous. Il n’y a plus de préparation possible.","color":"white"}]
execute unless score @s QUEST_VN_ASCENSION matches 20..100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_ASCENSION matches 20..100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Ce transport n’est pas un banc d’observation. Revenez si nous avons réellement quelque chose à poursuivre.","color":"white"}]
