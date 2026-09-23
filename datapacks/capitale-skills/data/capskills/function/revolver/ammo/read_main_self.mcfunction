# Prototype 0.9.147: le barillet principal est autoritaire dans le scoreboard joueur.
# Initialisation à 6 uniquement si le joueur n'a encore aucune valeur valide.
execute unless score @s CAPREV_MAIN matches 0..6 if items entity @s weapon.mainhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_MAIN 6
