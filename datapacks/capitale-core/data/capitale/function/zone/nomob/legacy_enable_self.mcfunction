scoreboard players set #nomob_auto CAP_NOMOB_AUTO 1
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Zone anti-spawn] Nettoyage automatique legacy activé temporairement.","color":"gold"}
tellraw @s {"text":"À désactiver après peinture du biome capitale:capitale si tu veux éviter tout nettoyage automatique de mobs conservés.","color":"gray"}
