function capskills:integration/ensure_current_self
# Baguette d’Altération : clic droit = zone ou Aveuglement ; sneak + clic droit = Suspension si débloquée.
execute unless score @s CAPSK_ALCH_RANK matches 1.. run title @s actionbar {"text":"Vous n'avez pas débloqué l'Altération des Seuils.","color":"red"}
execute if score @s CAPSK_ALCH_RANK matches 1.. unless predicate capskills:player/is_sneaking run function capskills:skill/alchimie/cast_blind_or_zone_start_self
execute if score @s CAPSK_ALCH_RANK matches 1.. if predicate capskills:player/is_sneaking run function capskills:skill/alchimie/cast_levitate_or_zone_start_self
