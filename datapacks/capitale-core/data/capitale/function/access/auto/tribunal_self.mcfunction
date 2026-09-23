# Accès dérivé du rang — voie notifiée (à utiliser lors d’un vrai changement de rang).
scoreboard players set @s ACCESS_TRIBUNAL 2
tag @s add ACCESS_TRIBUNAL
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Tribunal reconnu par votre rang.","color":"green"}
