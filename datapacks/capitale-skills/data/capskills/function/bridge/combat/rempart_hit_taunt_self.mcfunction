# CapSkills 0.9.53 — clic gauche / hit avec le Relais du Rempart.
# Sans sneak : Root du Rempart.
# Avec sneak : Provocation impériale.
function capskills:integration/ensure_current_self

scoreboard players set #remp_left_any CAPSK_TMP 0

# Root : clic gauche normal uniquement.
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.root.r1] run scoreboard players set #remp_left_any CAPSK_TMP 1
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.root.r1] if score @s CAPSK_REMP_ROOT_CD matches 0 run function capskills:skill/rempart/root_apply_from_hit_self
execute unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.root.r1] if score @s CAPSK_REMP_ROOT_CD matches 1.. run title @s actionbar [{"text":"Root du Rempart en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_REMP_ROOT_CD"},"color":"blue"},{"text":" s","color":"blue"}]

# Provocation : sneak + clic gauche uniquement.
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.taunt_mmo.r1] run scoreboard players set #remp_left_any CAPSK_TMP 1
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.taunt_mmo.r1] if score @s CAPSK_TAUNT_CD matches 0 run function capskills:skill/rempart/taunt_start_self
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.taunt_mmo.r1] if score @s CAPSK_TAUNT_CD matches 1.. run title @s actionbar [{"text":"Provocation en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_TAUNT_CD"},"color":"blue"},{"text":" s","color":"blue"}]

execute if score #remp_left_any CAPSK_TMP matches 0 unless predicate capskills:player/is_sneaking run title @s actionbar {"text":"Root du Rempart non débloqué.","color":"red"}
execute if score #remp_left_any CAPSK_TMP matches 0 if predicate capskills:player/is_sneaking run title @s actionbar {"text":"Provocation impériale non débloquée.","color":"red"}
