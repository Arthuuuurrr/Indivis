# CapSkills 0.9.52 — prépare la Poigne depuis une cible cliquée directement par le packet.
# @s = tank ; cible = entité temporairement taggée capskills.mod_right_click_target par le jar.
tag @e[tag=capskills.mod_pull_target] remove capskills.mod_pull_target
execute as @e[tag=capskills.mod_right_click_target,limit=1,sort=nearest] run tag @s add capskills.mod_pull_target
execute if entity @e[tag=capskills.mod_pull_target,limit=1,sort=nearest] run function capskills:skill/rempart/grab_prepare_mod_self
