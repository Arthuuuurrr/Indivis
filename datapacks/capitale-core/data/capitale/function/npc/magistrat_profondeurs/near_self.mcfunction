# RC9ak — wrapper Near Distance universel.
# Fonctionne si EasyNPC l'exécute comme joueur OU comme PNJ.
execute if entity @s[type=player] run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/near_offer_self
execute unless entity @s[type=player] at @s as @a[distance=..12,limit=1,sort=nearest] run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/near_offer_self
