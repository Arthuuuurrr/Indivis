# CapSkills 0.9.56 — Poigne du Rempart : score init + fallback large + placement sûr.
# Objectif : rendre le grab testable même si le cooldown n'était pas initialisé ou si la cible est custom.
function capskills:integration/ensure_current_self
scoreboard players add @s CAPSK_REMP_PULL_CD 0
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @e[tag=capskills.mod_pull_target] remove capskills.mod_pull_target
tag @s remove capskills.mod_pull_pending

execute unless entity @s[tag=capskills.rempart.pull.r1] run title @s actionbar {"text":"Poigne du Rempart non débloquée.","color":"red"}
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 1.. run title @s actionbar [{"text":"Poigne du Rempart en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_REMP_PULL_CD"},"color":"blue"},{"text":" s","color":"blue"}]
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 run tag @s add capskills.pull_caster
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 anchored eyes rotated as @s positioned ^ ^ ^0.8 run function capskills:skill/rempart/grab_raycast_target
# Fallback large : cible vivante la plus proche dans 12 blocs, y compris mobs custom/EasyNPC si Health existe.
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 if score #hit CAPSK_TMP matches 0 run function capskills:skill/rempart/grab_nearest_fallback_self
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/rempart/grab_prepare_mod_self
execute if entity @s[tag=capskills.pull_caster] run tag @s remove capskills.pull_caster
execute if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Poigne du Rempart : aucune cible vivante dans 12 blocs.","color":"red"}
