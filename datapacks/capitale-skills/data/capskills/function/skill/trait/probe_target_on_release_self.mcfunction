# @s = archer. Sets #hit to 1 if a living target is in the release axis. Leaves temporary target tag for burst_start, which clears it again.
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set @s CAPSK_RAY_STEP 0
tag @e[tag=capskills.trait_target] remove capskills.trait_target
tag @s add capskills.trait_caster
execute at @s anchored eyes rotated as @s positioned ^ ^ ^0.75 run function capskills:skill/trait/raycast_target_silent
tag @s remove capskills.trait_caster
