
# 0.9.29 — Clic droit simple du Relais du Rempart.
# Si Root du Rempart I est débloqué et qu'une entité vivante est visée : root ciblé.
# Sinon : Rempart personnel, comme avant.
function capskills:integration/ensure_current_self
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set #can_root CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @s add capskills.caster
execute if entity @s[tag=capskills.rempart.root.r1] rotated as @s anchored eyes positioned ^ ^ ^0.6 run function capskills:skill/rempart/root_raycast_player
tag @s remove capskills.caster
execute if score #hit CAPSK_TMP matches 0 run function capskills:skill/rempart/cast_self_start_self
