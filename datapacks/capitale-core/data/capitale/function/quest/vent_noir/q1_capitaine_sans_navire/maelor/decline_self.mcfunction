function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_VN_CAPITAINE 2
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Je resterai là. Les hommes pressés finissent rarement du bon côté d'une histoire de pirates.","color":"white"}]
