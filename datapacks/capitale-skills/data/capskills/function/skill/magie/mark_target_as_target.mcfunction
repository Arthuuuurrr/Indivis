# @s = cible vivante detectee par le raycast magique.
scoreboard players set #hit CAPSK_TMP 1
tag @s add capskills.magic_target
execute at @s run particle minecraft:enchanted_hit ~ ~1.0 ~ 0.35 0.45 0.35 0.03 12 force @a[distance=..32]
execute at @s run particle minecraft:witch ~ ~1.0 ~ 0.55 0.65 0.55 0.04 28 force @a[distance=..32]
execute at @s run particle minecraft:dragon_breath ~ ~1.0 ~ 0.45 0.55 0.45 0.03 18 force @a[distance=..32]
execute at @s run playsound minecraft:block.amethyst_block.hit player @a[distance=..24] ~ ~ ~ 0.65 1.70 0
