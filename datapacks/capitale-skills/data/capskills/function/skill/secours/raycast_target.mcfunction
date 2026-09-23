# Raycast Baguette de Soins : ciblage joueur très généreux, avec corrections de hauteur et longue portée.
# Historique 0.8.3 : le raycast détecte une cible même si le soin ciblé est non débloqué ou en cooldown,
# afin de ne pas déclencher accidentellement le self-heal quand un allié est visé.
execute if score #hit CAPSK_TMP matches 0 as @a[distance=..1.65,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/secours/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.85 ~ as @a[distance=..1.85,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/secours/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-1.0 ~ as @a[distance=..1.95,gamemode=!spectator,tag=!capskills.caster,sort=nearest,limit=1] run function capskills:skill/secours/target_decision_as_target
execute if score #hit CAPSK_TMP matches 0 unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:cave_air unless block ~ ~ ~ minecraft:void_air run scoreboard players set #block CAPSK_TMP 1
scoreboard players add @a[tag=capskills.caster,limit=1] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 if score #block CAPSK_TMP matches 0 as @a[tag=capskills.caster,limit=1] if score @s CAPSK_RAY_STEP matches ..112 positioned ^ ^ ^0.5 run function capskills:skill/secours/raycast_target
