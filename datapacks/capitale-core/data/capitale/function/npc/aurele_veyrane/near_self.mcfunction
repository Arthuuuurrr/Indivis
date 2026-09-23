# RC9ak — alias Aurèle Veyrane, wrapper universel.
execute if entity @s[type=player] run function capitale:quest/sous_le_regard_du_coeur/aurele/near_offer_self
execute unless entity @s[type=player] at @s as @a[distance=..12,limit=1,sort=nearest] run function capitale:quest/sous_le_regard_du_coeur/aurele/near_offer_self
