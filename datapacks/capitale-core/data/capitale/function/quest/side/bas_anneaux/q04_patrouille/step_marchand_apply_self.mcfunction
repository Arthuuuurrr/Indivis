scoreboard players set @s QUEST_SIDE_BA_Q04 50
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.45 1.2
tellraw @s [{"text":"[Marchand inquiet]","color":"aqua"},{"text":" : Voilà le garçon. Il glisse les mains dans les paniers dès que les gardes tournent la tête.","color":"white"}]
tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Retourner voir Roland pour décider de la suite.","color":"white"}]
