# CapSkills 0.9.56 — Poigne du Rempart : applique un grab avec placement sûr.
# @s = tank ; target porte capskills.mod_pull_target.
tag @s add capskills.mod_pull_pending

# Placement sûr : évite de téléporter la cible dans les murs/le sol quand le joueur vise vers un bloc.
execute if entity @e[tag=capskills.mod_pull_target,limit=1,sort=nearest] run function capskills:skill/rempart/grab_safe_place_self

# Succès : cooldown et feedback.
execute if score #placed CAPSK_TMP matches 1 run scoreboard players set @s CAPSK_REMP_PULL_CD 34
execute if score #placed CAPSK_TMP matches 1 run effect give @e[tag=capskills.mod_pull_target,limit=1,sort=nearest] minecraft:slowness 1 2 true
execute if score #placed CAPSK_TMP matches 1 at @e[tag=capskills.mod_pull_target,limit=1,sort=nearest] run particle minecraft:electric_spark ~ ~1.2 ~ 0.35 0.35 0.35 0.04 18 force @a[distance=..32]
execute if score #placed CAPSK_TMP matches 1 at @s run particle minecraft:enchant ~ ~1.2 ~ 0.7 0.35 0.7 0.06 24 force @a[distance=..32]
execute if score #placed CAPSK_TMP matches 1 at @s run playsound minecraft:block.chain.place player @a[distance=..28] ~ ~ ~ 0.65 0.75 0
execute if score #placed CAPSK_TMP matches 1 run title @s actionbar {"text":"Poigne du Rempart : cible ramenée en zone sûre. Recharge : 34 s.","color":"blue"}

# Échec : la cible existe, mais aucun point libre n'a été trouvé autour du tank. Pas de cooldown consommé.
execute unless score #placed CAPSK_TMP matches 1 run title @s actionbar {"text":"Poigne du Rempart : aucun point d'arrivée libre devant vous.","color":"red"}
