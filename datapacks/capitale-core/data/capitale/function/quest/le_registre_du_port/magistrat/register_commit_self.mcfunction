
execute if score @s CAP_RANGSOCIAL matches ..9 run scoreboard players set @s CAP_RANGSOCIAL 10
function capitale:bounds/rangsocial_self
function capitale:access/recalculate_by_rank_self
function capitale:display/prefix/sync_self
execute if score @s CAP_RANGSOCIAL matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches 10 run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Votre arrivée est enregistrée. Pour la Couronne, vous êtes désormais un visiteur toléré dans la Haute Capitale.","color":"white"}]
execute if score @s CAP_RANGSOCIAL matches 11.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches 11.. run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Votre arrivée est portée au registre. Votre qualité était déjà reconnue au-delà d’un simple visiteur ; le Port en conserve désormais trace à son tour.","color":"white"}]
execute if score @s QUEST_GARDEPORT matches 30 run scoreboard players set @s QUEST_GARDEPORT 40
execute if score @s QUEST_GARDEPORT matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_GARDEPORT matches 40 run tellraw @s {"text":"[Quête] Le registre du Port — Retournez voir Léovic.","color":"gold"}
execute if score @s QUEST_GARDEPORT matches 40 run title @s times 5 50 15
execute if score @s QUEST_GARDEPORT matches 40 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_GARDEPORT matches 40 run title @s subtitle {"text":"Retournez voir Léovic.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
