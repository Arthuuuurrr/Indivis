# CapSkills 0.9.1 — Provocation du Rempart MOD-ONLY, sans fallback datapack.
# @s = joueur tank qui vient de toucher une cible avec le Relais du Rempart.
# Important : cette fonction ne touche plus les mobs, n'inflige plus de dégât symbolique,
# et ne simule plus l'aggro. Elle prépare uniquement la demande consommée par le mod.
function capskills:integration/ensure_current_self
scoreboard players set #taunt_request CAPSK_TMP 0
execute unless entity @s[tag=capskills.rempart.taunt_mmo.r1] run title @s actionbar {"text":"Provocation impériale non débloquée.","color":"red"}
execute if entity @s[tag=capskills.rempart.taunt_mmo.r1] unless score @s CAPSK_BASTION_RANK matches 1.. run title @s actionbar {"text":"Bastion requis pour provoquer.","color":"red"}
execute if entity @s[tag=capskills.rempart.taunt_mmo.r1] if score @s CAPSK_BASTION_RANK matches 1.. if score @s CAPSK_TAUNT_CD matches 1.. run title @s actionbar {"text":"Provocation en recharge.","color":"blue"}
execute if entity @s[tag=capskills.rempart.taunt_mmo.r1] if score @s CAPSK_BASTION_RANK matches 1.. if score @s CAPSK_TAUNT_CD matches 0 run scoreboard players set #taunt_request CAPSK_TMP 1
execute if score #taunt_request CAPSK_TMP matches 1 run tag @s add capskills.mod_hard_taunt.pending
execute if score #taunt_request CAPSK_TMP matches 1 if score @s CAPSK_BASTION_RANK matches 1 run tag @s add capskills.mod_hard_taunt.r1
execute if score #taunt_request CAPSK_TMP matches 1 if score @s CAPSK_BASTION_RANK matches 2.. run tag @s add capskills.mod_hard_taunt.r2
execute if score #taunt_request CAPSK_TMP matches 1 run scoreboard players set @s CAPSK_TAUNT_CD 24
execute if score #taunt_request CAPSK_TMP matches 1 if entity @s[tag=capskills.rempart.ancrage.r1] run scoreboard players set @s CAPSK_TAUNT_CD 22
execute if score #taunt_request CAPSK_TMP matches 1 at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:angry_villager ~ ~1.45 ~ 0.18 0.25 0.18 0.01 6 force @a[distance=..24]
execute if score #taunt_request CAPSK_TMP matches 1 at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:crit ~ ~1.0 ~ 0.25 0.35 0.25 0.03 10 force @a[distance=..24]
execute if score #taunt_request CAPSK_TMP matches 1 at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run playsound minecraft:block.note_block.bass player @a[distance=..18] ~ ~ ~ 0.35 0.75 0
execute if score #taunt_request CAPSK_TMP matches 1 at @s run particle minecraft:angry_villager ~ ~1.5 ~ 1.8 0.8 1.8 0.04 24 force @a[distance=..24]
execute if score #taunt_request CAPSK_TMP matches 1 at @s run playsound minecraft:entity.warden.angry player @a[distance=..20] ~ ~ ~ 0.45 1.35 0
execute if score #taunt_request CAPSK_TMP matches 1 at @s run function capskills:visual/activation/rempart_taunt_self
execute if score #taunt_request CAPSK_TMP matches 1 run title @s actionbar {"text":"Provocation impériale déclenchée. Recharge active.","color":"blue"}
execute if score #taunt_request CAPSK_TMP matches 1 run tellraw @s [{"text":"[CapSkills] ","color":"dark_gray"},{"text":"Provocation impériale déclenchée","color":"blue"},{"text":" — le mod force l’aggro si la cible est compatible.","color":"gray"}]
