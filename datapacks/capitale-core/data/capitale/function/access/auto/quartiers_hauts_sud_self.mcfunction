# Accès dérivé du rang — voie notifiée (à utiliser lors d’un vrai changement de rang).
scoreboard players set @s ACCESS_QH_SUD 2
tag @s add ACCESS_QUARTIERS_HAUTS_SUD
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Quartiers hauts sud reconnu par votre rang.","color":"green"}
