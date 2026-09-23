# Exécuté comme la cible ; le lanceur porte temporairement le tag caster.
tag @s add capskills_0119.lance_longue.target
scoreboard players operation @s CAPSK_LANCE_OWNER = @a[tag=capskills_0119.lance_longue.caster,limit=1,sort=nearest] CAPSK_UID
tag @a[tag=capskills_0119.lance_longue.caster,limit=1,sort=nearest] add capskills_0119.lance_longue.target_locked
scoreboard players set #lance_hit CAPSK_LANCE_RAY 1
