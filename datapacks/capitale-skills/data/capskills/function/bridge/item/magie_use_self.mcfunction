function capskills:integration/ensure_current_self
# Baguette de Destruction : clic droit = Décharge ; sneak + clic droit = Marque corrosive si débloquée.
execute unless score @s CAPSK_MAG_RANK matches 1.. run title @s actionbar {"text":"Vous n'avez pas débloqué la magie du Cœur.","color":"red"}
execute if score @s CAPSK_MAG_RANK matches 1.. if predicate capskills:player/is_sneaking if entity @s[tag=capskills.magie.dot.r1] run function capskills:skill/magie/cast_dot_target_start_self
execute if score @s CAPSK_MAG_RANK matches 1.. if predicate capskills:player/is_sneaking unless entity @s[tag=capskills.magie.dot.r1] run title @s actionbar {"text":"Marque corrosive non débloquée.","color":"red"}
execute if score @s CAPSK_MAG_RANK matches 1.. unless predicate capskills:player/is_sneaking run function capskills:skill/magie/cast_target_start_self
