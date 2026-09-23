# CapSkills 0.9.56 — Relais du Rempart : input stable + grab safe placement.
# Sneak + clic droit : Égide de zone.
# Clic droit simple : tente Poigne du Rempart ; si aucune cible, Rempart personnel.
# Clic gauche / hit : Root sans sneak ; Provocation avec sneak.
function capskills:integration/ensure_current_self
scoreboard players add @s CAPSK_REMP_PULL_CD 0

tag @s remove capskills.mod_hard_taunt.pending
tag @s remove capskills.mod_hard_taunt.r1
tag @s remove capskills.mod_hard_taunt.r2
tag @s remove capskills.mod_pull_pending
tag @e[tag=capskills.mod_pull_target] remove capskills.mod_pull_target
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0

execute unless score @s CAPSK_REMP_RANK matches 1.. run title @s actionbar {"text":"Vous n'avez pas débloqué la Voie du Rempart.","color":"red"}

# Égide : entrée réservée.
execute if score @s CAPSK_REMP_RANK matches 1.. if predicate capskills:player/is_sneaking run function capskills:skill/rempart/cast_zone_self

# Poigne : clic droit simple, uniquement si le nœud est débloqué et le cooldown prêt.
execute if score @s CAPSK_REMP_RANK matches 1.. unless predicate capskills:player/is_sneaking unless entity @s[tag=capskills.rempart.pull.r1] run title @s actionbar {"text":"Poigne du Rempart non débloquée : Rempart personnel.","color":"blue"}
execute if score @s CAPSK_REMP_RANK matches 1.. unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 0 run function capskills:skill/rempart/grab_start_self

# Si la Poigne est en recharge, on affiche l'info mais on laisse le Rempart personnel possible.
execute if score @s CAPSK_REMP_RANK matches 1.. unless predicate capskills:player/is_sneaking if entity @s[tag=capskills.rempart.pull.r1] if score @s CAPSK_REMP_PULL_CD matches 1.. run title @s actionbar [{"text":"Poigne du Rempart en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_REMP_PULL_CD"},"color":"blue"},{"text":" s","color":"blue"}]

# Fallback : si aucun grab n'a trouvé de cible, le clic droit redevient le Rempart personnel stable.
execute if score @s CAPSK_REMP_RANK matches 1.. unless predicate capskills:player/is_sneaking if score #hit CAPSK_TMP matches 0 run function capskills:skill/rempart/cast_self_start_self
