# 0.8.25 — récupération du matériel d'alchimie via /trigger CAPSK_ALCH_CLAIM.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.alchimiste.kit.r1] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Matériel d’alchimie indisponible : compétence Alchimiste I requise.","color":"red"}]
execute if entity @s[tag=capskills.alchimiste.kit.r1] unless entity @s[tag=capskills.alch.kit.claimed] run function capskills:reward/alchemy/grant_self
execute if entity @s[tag=capskills.alchimiste.kit.r1,tag=capskills.alch.kit.claimed] run function capskills:reward/alchemy/check_claimed_self
