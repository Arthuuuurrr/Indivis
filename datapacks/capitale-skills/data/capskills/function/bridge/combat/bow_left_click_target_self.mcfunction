# CapSkills 0.9.58 — clic gauche arc sur entité : Flèche d’entrave directe.
# @s = archer ; cible temporaire taggée par le jar : capskills.mod_hit_target.
function capskills:integration/ensure_current_self
scoreboard players add @s CAPSK_BOW_ENTRAVE_CD 0
scoreboard players add @s CAPSK_BOW_ENTRAVE_T 0
execute unless entity @s[tag=capskills.trait.fleche_entrave.r1] unless entity @s[tag=capskills.skill.trait_fleche_entrave_1] run title @s actionbar {"text":"Flèche d’entrave non débloquée.","color":"red"}
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 1.. run title @s actionbar [{"text":"Flèche d’entrave en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_BOW_ENTRAVE_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 unless entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Flèche d’entrave : aucune cible.","color":"yellow"}
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.trait_caster
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] at @s run function capskills:skill/trait/fleche_entrave_impact_as_target
execute if entity @s[tag=capskills.trait_caster] run scoreboard players set @s CAPSK_BOW_ENTRAVE_CD 18
tag @s remove capskills.trait_caster
