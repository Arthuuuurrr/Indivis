# Wrapper EasyNPC robuste :
# - Execute as user ON : @s = joueur
# - Execute as user OFF avec @s = PNJ/marker : le joueur est cherché autour de la lanterne.
# - Fallback : si la source n'est pas une entité, on utilise la position courante de la commande.
function capitale:dialogue/anchor/create_self
execute if entity @s[type=minecraft:player] run function capitale:quest/divers/lampe_manquante/globe/lanterne_4_player
execute if entity @s[type=!minecraft:player] at @s as @p[distance=..8,sort=nearest,limit=1] run function capitale:quest/divers/lampe_manquante/globe/lanterne_4_player
execute unless entity @s as @p[distance=..8,sort=nearest,limit=1] run function capitale:quest/divers/lampe_manquante/globe/lanterne_4_player
