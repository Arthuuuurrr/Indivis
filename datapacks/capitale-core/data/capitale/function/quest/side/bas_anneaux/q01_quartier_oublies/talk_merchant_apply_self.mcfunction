scoreboard players set @s QUEST_SIDE_BA_Q01 30
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.45 1.2
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Marchand]","color":"aqua"},{"text":" : Elle m’a encore volé une pomme. Si vous la retrouvez, dites-lui que cette fois je préviens la Garde.","color":"white"}]
tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Retrouver Mira et décider quoi faire.","color":"white"}]
