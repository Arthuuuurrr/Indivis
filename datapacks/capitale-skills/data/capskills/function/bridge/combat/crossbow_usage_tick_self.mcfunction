# CapSkills BETA 0.9.13 — hook direct Item.usageTick arbalète, conservé comme chemin secondaire.
# Ce bridge est le seul à poser le tag usage_tick : la salve démarre donc sur vrai sneak + clic droit,
# puis le tick serveur maintient la canalisation tant que le joueur reste accroupi avec l’arbalète.
function capskills:integration/ensure_current_self
execute unless predicate capskills:player/is_sneaking run title @s actionbar {"text":"Salve préparée : accroupis-toi pour canaliser.","color":"yellow"}
execute if predicate capskills:player/is_sneaking run tag @s add capskills.arbalete.salve.usage_tick
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 unless entity @s[tag=capskills.arbalete.salve.channeling] run function capskills:skill/arbalete/salve_prepare_start_self
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.arbalete.salve.channeling] run function capskills:skill/arbalete/salve_prepare_tick_self
