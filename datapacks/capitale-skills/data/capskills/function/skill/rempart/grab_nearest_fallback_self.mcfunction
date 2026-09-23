# CapSkills 0.9.56 — fallback large : cible vivante la plus proche autour du tank.
# @s = tank. Ne dépend pas du tag #pullable pour accepter mobs custom/EasyNPC.
# Portée volontairement portée à 12 blocs pour une vraie Poigne/Grab de tank.
execute as @e[tag=!capskills.pull_caster,distance=0.15..12.0,type=!minecraft:item,type=!minecraft:experience_orb,type=!minecraft:area_effect_cloud,type=!minecraft:marker,type=!minecraft:armor_stand,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/rempart/grab_target_decision_as_target
