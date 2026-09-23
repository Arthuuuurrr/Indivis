# Wrapper robuste EasyNPC : fonctionne avec Execute as user ON ou OFF.
execute if entity @s[type=minecraft:player] run function capitale:quest/divers/lampe_manquante/veilleuse/near_player
execute unless entity @s[type=minecraft:player] as @p[distance=..8,sort=nearest,limit=1] run function capitale:quest/divers/lampe_manquante/veilleuse/near_player
