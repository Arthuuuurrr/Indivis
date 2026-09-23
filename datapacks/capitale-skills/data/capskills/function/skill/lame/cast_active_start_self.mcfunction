function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.lame.fente.r1] run title @s actionbar {"text":"Fente impériale non débloquée.","color":"red"}
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 1.. run title @s actionbar {"text":"Fente impériale en recharge.","color":"red"}
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 run scoreboard players set #hit CAPSK_TMP 0
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 run tag @e[tag=capskills.lame_target] remove capskills.lame_target
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 run tag @s add capskills.lame_caster
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..18] ~ ~ ~ 0.70 1.20 0
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^0.85 run function capskills:skill/lame/raycast_target
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/lame/apply_active_from_caster_self
execute if entity @s[tag=capskills.lame.fente.r1] if score @s CAPSK_LAME_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Aucune cible à portée de fente.","color":"yellow"}
tag @s remove capskills.lame_caster
tag @e[tag=capskills.lame_target] remove capskills.lame_target
