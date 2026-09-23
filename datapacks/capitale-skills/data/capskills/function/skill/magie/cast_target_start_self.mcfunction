function capskills:integration/ensure_current_self
# Baguette de Destruction — cast depuis le lanceur (@s = joueur).
# 0.7.0 : raycast sans tag entity_type ; detection par presence du NBT Health.
execute if score @s CAPSK_MAGIE_CD matches 1.. run title @s actionbar {"text":"Baguette de Destruction en recharge.","color":"light_purple"}
execute if score @s CAPSK_MAGIE_CD matches 0 run scoreboard players set #hit CAPSK_TMP 0
execute if score @s CAPSK_MAGIE_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if score @s CAPSK_MAGIE_CD matches 0 run tag @e[tag=capskills.magic_target] remove capskills.magic_target
execute if score @s CAPSK_MAGIE_CD matches 0 run tag @s add capskills.caster
execute if score @s CAPSK_MAGIE_CD matches 0 at @s run particle minecraft:witch ~ ~1.2 ~ 0.30 0.30 0.30 0.04 18 force @a[distance=..24]
execute if score @s CAPSK_MAGIE_CD matches 0 at @s run particle minecraft:dragon_breath ~ ~1.2 ~ 0.20 0.20 0.20 0.02 8 force @a[distance=..24]
execute if score @s CAPSK_MAGIE_CD matches 0 at @s run playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.70 1.65 0
execute if score @s CAPSK_MAGIE_CD matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^0.65 run function capskills:skill/magie/raycast_target
execute if score @s CAPSK_MAGIE_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/magie/apply_target_from_caster_self
execute if score @s CAPSK_MAGIE_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Aucune cible magique dans l’axe.","color":"yellow"}
tag @s remove capskills.caster
tag @e[tag=capskills.magic_target] remove capskills.magic_target
