# Accès dérivé du rang — voie notifiée (à utiliser lors d’un vrai changement de rang).
scoreboard players set @s ACCESS_SEUILS 2
tag @s add ACCESS_SEUILS_SCELLES
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Portails scellés reconnu par votre rang.","color":"green"}
