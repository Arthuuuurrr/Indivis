
function capitale:quest/divers/acte_dette/document/give_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Vous avez égaré l’acte ? Prenez cette copie. S’il faut choisir, autant le faire avec quelque chose dans les mains.","color":"white"}]
execute at @s run playsound minecraft:entity.item.pickup master @s ~ ~ ~ 0.55 1.1
