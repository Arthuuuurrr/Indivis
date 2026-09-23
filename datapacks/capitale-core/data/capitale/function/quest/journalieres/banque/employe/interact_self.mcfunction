# Wrapper robuste EasyNPC : fonctionne avec Execute as user ON ou OFF.
execute if entity @s[type=minecraft:player] run function capitale:quest/journalieres/banque/employe/interact_player
execute unless entity @s[type=minecraft:player] as @p[distance=..8,sort=nearest,limit=1] run function capitale:quest/journalieres/banque/employe/interact_player
