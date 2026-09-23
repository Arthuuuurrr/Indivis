function capitale:quest/guidage/sync_self
execute if score @s CAP_GUIDE_LOCK matches 1.. run function capitale:quest/blocked_guide_self
execute unless score @s CAP_GUIDE_LOCK matches 1.. run function capitale:quest/journalieres/registre_port/commis/accept_body_154_self
