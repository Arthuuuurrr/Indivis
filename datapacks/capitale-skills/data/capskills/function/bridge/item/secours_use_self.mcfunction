function capskills:integration/ensure_current_self
# Baguette de Soins : clic droit = soin personnel ou ciblé ; sneak + clic droit = zone.
execute if predicate capskills:player/is_sneaking if score @s CAPSK_SEC_RANK matches 1.. run function capskills:skill/secours/cast_zone_self
execute if predicate capskills:player/is_sneaking unless score @s CAPSK_SEC_RANK matches 1.. run title @s actionbar {"text":"Cercle de soin non débloqué : prenez Secours impérial I.","color":"red"}
execute unless predicate capskills:player/is_sneaking if score @s CAPSK_SEC_RANK matches 1.. run function capskills:skill/secours/cast_target_or_self_start_self
execute unless predicate capskills:player/is_sneaking unless score @s CAPSK_SEC_RANK matches 1.. run function capskills:skill/commun/cast_self_heal_self
