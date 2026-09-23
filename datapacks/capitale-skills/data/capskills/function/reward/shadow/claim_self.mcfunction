# 0.8.30-rc4 — récupération de la Cache de l’Ombre via /trigger CAPSK_SHADOW_CLAIM.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.ombre.cache.r1] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre indisponible : compétence Cache de l’Ombre I requise.","color":"red"}]
execute if entity @s[tag=capskills.ombre.cache.r1] unless entity @s[tag=capskills.shadow.kit.claimed] run function capskills:reward/shadow/grant_self
execute if entity @s[tag=capskills.ombre.cache.r1,tag=capskills.shadow.kit.claimed] run function capskills:reward/shadow/check_claimed_self
