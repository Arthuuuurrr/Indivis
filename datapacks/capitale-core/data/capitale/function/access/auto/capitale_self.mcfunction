# Accès dérivé du rang — voie notifiée (à utiliser lors d’un vrai changement de rang).
scoreboard players set @s ACCESS_CAPITALE 2
tag @s add ACCESS_CAPITALE
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Capitale reconnu par votre rang.","color":"green"}
