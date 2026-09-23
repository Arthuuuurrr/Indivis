# RC9ak — wrapper Near Distance universel.
# Fonctionne si EasyNPC l'exécute comme joueur OU comme PNJ.
execute if entity @s[type=player] run function capitale:npc/geraud_rivet/near_logic_self
execute unless entity @s[type=player] at @s as @a[distance=..12,limit=1,sort=nearest] run function capitale:npc/geraud_rivet/near_logic_self
