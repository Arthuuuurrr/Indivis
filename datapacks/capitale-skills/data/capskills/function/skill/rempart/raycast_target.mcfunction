# Raycast Relais du Rempart — cible les joueurs alliés.
# Historique 0.8.3 : le raycast détecte une cible même si Égide I manque ou est en cooldown,
# afin de ne pas déclencher accidentellement le Rempart personnel quand un allié est visé.
execute if score #hit CAPSK_TMP matches 0 as @a[distance=..1.35,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/rempart/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.8 ~ as @a[distance=..1.55,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/rempart/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-1.6 ~ as @a[distance=..1.7,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/rempart/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:cave_air unless block ~ ~ ~ minecraft:void_air run scoreboard players set #block CAPSK_TMP 1
scoreboard players add @a[tag=capskills.caster,limit=1] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 if score #block CAPSK_TMP matches 0 as @a[tag=capskills.caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..44 positioned ^ ^ ^0.4 run function capskills:skill/rempart/raycast_target
