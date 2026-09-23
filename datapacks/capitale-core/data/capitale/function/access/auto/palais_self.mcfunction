# Accès dérivé du rang — voie notifiée (à utiliser lors d’un vrai changement de rang).
scoreboard players set @s ACCESS_PALAIS 2
tag @s add ACCESS_PALAIS
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Palais reconnu par votre rang.","color":"green"}
