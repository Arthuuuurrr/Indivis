# Usage sneak du Baguette de Soins.
# Si un joueur est visé : tentative de soin ciblé, avec son cooldown propre.
# Si aucun joueur n'est visé : cercle de soin, avec son cooldown propre.
function capskills:integration/ensure_current_self
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @s add capskills.caster
execute rotated as @s anchored eyes positioned ^ ^ ^0.5 run function capskills:skill/secours/raycast_target
tag @s remove capskills.caster
execute if score #hit CAPSK_TMP matches 0 run function capskills:skill/secours/cast_zone_self
