# LEGACY 0.8.4 : ancien usage non-sneak cible puis fallback self.
# Conservé pour compatibilité interne, mais le bridge principal utilise désormais
# capskills:skill/secours/cast_target_or_zone_start_self en sneak.
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @s add capskills.caster
execute rotated as @s anchored eyes positioned ^ ^ ^0.5 run function capskills:skill/secours/raycast_target
tag @s remove capskills.caster
execute if score #hit CAPSK_TMP matches 0 run function capskills:skill/commun/cast_self_heal_self
