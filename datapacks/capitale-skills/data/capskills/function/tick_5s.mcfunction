# CapSkills 0.9.152 — scans non critiques regroupés à 5 secondes.
scoreboard players set #slow CAPSK_CLOCK 0
execute as @a run function capskills:integration/login_prompt_self
# 0.10.18g : doctrine_root reste automatiquement débloqué, y compris après un reset.
execute as @a[tag=!capskills.skill.doctrine_root] run puffish_skills skills unlock @s capskills:doctrine doctrine_root
tag @a[tag=!capskills.skill.doctrine_root] add capskills.skill.doctrine_root
execute as @a[tag=capskills.mineur.bonus_ores.r1] run function capskills:mechanics/mining/check_self
execute as @a[tag=capskills.forge.r1] run function capskills:mechanics/forge/check_self
execute as @a[tag=capskills.farming.r1] run function capskills:mechanics/farming/check_self
