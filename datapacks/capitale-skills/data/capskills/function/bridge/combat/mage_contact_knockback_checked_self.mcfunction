# 0.9.26 — logique vérifiée du clic gauche Baguette de Destruction. Appelée après garde anti-double-trigger.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.magie.contact.r1] run title @s actionbar {"text":"Onde de recul non débloquée.","color":"red"}
execute if entity @s[tag=capskills.magie.contact.r1] if score @s CAPSK_MAGE_CONTACT_CD matches 1.. run title @s actionbar [{"text":"Onde de recul en recharge : ","color":"light_purple"},{"score":{"name":"@s","objective":"CAPSK_MAGE_CONTACT_CD"},"color":"light_purple"},{"text":" s","color":"light_purple"}]
execute if entity @s[tag=capskills.magie.contact.r1] if score @s CAPSK_MAGE_CONTACT_CD matches 0 unless entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Aucune cible de contact détectée.","color":"yellow"}
execute if entity @s[tag=capskills.magie.contact.r1] if score @s CAPSK_MAGE_CONTACT_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run function capskills:skill/magie/contact_knockback_apply_self
