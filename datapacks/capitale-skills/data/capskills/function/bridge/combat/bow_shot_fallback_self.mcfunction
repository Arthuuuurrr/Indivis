# CapSkills 0.8.19 — fallback datapack Rafale.
# Si le hook mod sneak + clic droit arc ne démarre pas la canalisation, un vrai tir d'arc en sneak déclenche la Rafale.
# @s = joueur qui vient d'utiliser un arc vanilla.
function capskills:integration/ensure_current_self

# 0.9.57 : Flèche d’entrave préparée au clic gauche.
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] run function capskills:skill/trait/fleche_entrave_fire_self
execute unless predicate capskills:player/is_sneaking run scoreboard players set @s CAPSK_BOW_USED 0
execute if predicate capskills:player/is_sneaking unless entity @s[tag=capskills.trait.rafale.r1] run title @s actionbar {"text":"Rafale non débloquée.","color":"red"}
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 1.. run title @s actionbar {"text":"Rafale réglementaire en recharge.","color":"yellow"}
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 at @s run particle minecraft:crit ~ ~1.2 ~ 0.25 0.25 0.25 0.04 16 force @a[distance=..24]
execute if predicate capskills:player/is_sneaking if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run function capskills:skill/trait/cast_rafale_fire_checked_self
scoreboard players set @s CAPSK_BOW_USED 0
