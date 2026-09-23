# Baguette de Soins 0.8.16 — clic droit simple.
# Si un joueur est visé : soin ciblé.
# Si aucun joueur n’est visé : soin personnel.
function capskills:integration/ensure_current_self
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @s add capskills.caster
execute rotated as @s anchored eyes positioned ^ ^ ^0.5 run function capskills:skill/secours/raycast_target
tag @s remove capskills.caster
execute if score #hit CAPSK_TMP matches 0 run function capskills:skill/commun/cast_self_heal_self
