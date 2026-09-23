function capitale:quest/guidage/sync_self
execute if score @s CAP_GUIDE_LOCK matches 1.. run function capitale:quest/blocked_guide_self
execute unless score @s CAP_GUIDE_LOCK matches 1.. run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/accept_body_154_self
