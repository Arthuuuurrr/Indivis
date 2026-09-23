scoreboard players set @s QUEST_SIDE_BA_Q03 40
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.45 1.1
tellraw @s [{"text":"[Greffier]","color":"aqua"},{"text":" : Déposé, daté, classé. Voilà trois mots qui évitent beaucoup de cris.","color":"white"}]
function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/remind_final_delivery_self
