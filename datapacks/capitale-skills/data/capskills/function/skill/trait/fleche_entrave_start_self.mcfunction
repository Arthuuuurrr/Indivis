# CapSkills 0.9.57 — Flèche d’entrave I, préparation au clic gauche avec arc.
# @s = archer.
function capskills:integration/ensure_current_self
scoreboard players add @s CAPSK_BOW_ENTRAVE_CD 0
scoreboard players add @s CAPSK_BOW_ENTRAVE_T 0
execute unless entity @s[tag=capskills.trait.fleche_entrave.r1] run title @s actionbar {"text":"Flèche d’entrave non débloquée.","color":"red"}
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 1.. run title @s actionbar [{"text":"Flèche d’entrave en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_BOW_ENTRAVE_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 if entity @s[tag=capskills.trait.fleche_entrave.loaded] run title @s actionbar {"text":"Flèche d’entrave déjà préparée.","color":"yellow"}
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 unless entity @s[tag=capskills.trait.fleche_entrave.loaded] run tag @s add capskills.trait.fleche_entrave.loaded
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 run scoreboard players set @s CAPSK_BOW_ENTRAVE_T 12
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] if score @s CAPSK_BOW_ENTRAVE_CD matches 0 run scoreboard players set @s CAPSK_BOW_ENTRAVE_CD 20
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] if score @s CAPSK_BOW_ENTRAVE_CD matches 20 at @s run particle minecraft:enchanted_hit ~ ~1.1 ~ 0.30 0.22 0.30 0.03 14 force @a[distance=..28]
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] if score @s CAPSK_BOW_ENTRAVE_CD matches 20 at @s run playsound minecraft:item.crossbow.loading_middle player @s ~ ~ ~ 0.45 1.55 0
execute if entity @s[tag=capskills.trait.fleche_entrave.loaded] if score @s CAPSK_BOW_ENTRAVE_CD matches 20 run title @s actionbar {"text":"Flèche d’entrave prête — prochain tir.","color":"yellow"}
