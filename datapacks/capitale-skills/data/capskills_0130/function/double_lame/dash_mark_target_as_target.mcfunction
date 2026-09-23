# Exécutée comme la cible pendant que le lanceur porte dash_caster_context.
tag @s add capskills_0130.double_lame.dash_target
scoreboard players operation @s CAPSK_DASH_OWNER = @a[tag=capskills_0130.double_lame.dash_caster_context,limit=1] CAPSK_UID
