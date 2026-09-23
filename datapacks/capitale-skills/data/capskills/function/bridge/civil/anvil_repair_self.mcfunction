# Appelé par le mod 1.1.8 quand une réparation d’objet est détectée après usage récent d’une enclume.
execute unless score @s CAPSK_ANVIL_CD matches 1.. if entity @s[tag=capskills.enclume.repair.r1] run function capskills:reward/forge/anvil_refund_self
scoreboard players set @s CAPSK_ANVIL_CD 5
