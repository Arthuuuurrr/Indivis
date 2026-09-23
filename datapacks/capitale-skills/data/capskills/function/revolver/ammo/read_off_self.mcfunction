# Prototype 0.9.147: le barillet secondaire est autoritaire dans le scoreboard joueur.
execute unless score @s CAPREV_OFF matches 0..6 if items entity @s weapon.offhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_OFF 6
