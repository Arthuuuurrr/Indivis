# CapSkills 0.9.52 — Relais : clic droit direct sur entité.
# @s = tank ; la cible cliquée porte capskills.mod_right_click_target pendant l'appel Java.
function capskills:integration/ensure_current_self

# Sneak reste réservé à l'Égide, même si le curseur est sur une entité.
execute if predicate capskills:player/is_sneaking run function capskills:skill/rempart/cast_zone_self

# Clic droit entité sans sneak = Poigne du Rempart.
execute unless predicate capskills:player/is_sneaking unless entity @s[tag=capskills.rempart.pull.r1] run title @s actionbar {"text":"Poigne du Rempart non débloquée.","color":"red"}
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 1.. run title @s actionbar [{"text":"Poigne du Rempart en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_REMP_PULL_CD"},"color":"blue"},{"text":" s","color":"blue"}]
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 unless entity @e[tag=capskills.mod_right_click_target,limit=1,sort=nearest] run title @s actionbar {"text":"Poigne du Rempart : aucune cible transmise par le mod.","color":"red"}
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 if entity @e[tag=capskills.mod_right_click_target,limit=1,sort=nearest] run function capskills:skill/rempart/grab_prepare_from_right_click_self
