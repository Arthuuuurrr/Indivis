scoreboard players set @s QUEST_DIVERS_LAMPE 20
give @s minecraft:lantern[minecraft:custom_model_data={strings:['lampe_veille_profondeurs']},minecraft:custom_name=[{"text":"Lampe de veille retrouvée","italic":false,"color":"gold"}],minecraft:lore=[[{"text":"Lampe marquée d’une ferrure sombre et d’un anneau de cuivre.","italic":false,"color":"gray"}]]] 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Passage sombre]","color":"yellow"},{"text":" : Sous une marche, vous retrouvez la lampe. La ferrure est froide, mais l’anneau de cuivre correspond aux paroles de la Veilleuse.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Rapportez la lampe à la Veilleuse des Profondeurs.","color":"yellow"}
title @s times 5 50 15
title @s title {"text":"Lampe retrouvée","color":"gold","bold":true}
title @s subtitle {"text":"Retournez voir la Veilleuse","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.1 1.45
