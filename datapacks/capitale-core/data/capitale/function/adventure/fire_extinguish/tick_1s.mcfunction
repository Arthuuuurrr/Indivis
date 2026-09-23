# capitale_core 1.5.6-RC9AO — extinction du feu en mode Aventure.
# Les objets avec un can_break existant ne sont jamais écrasés.
# Compatibilité : armes/outils vanilla, objets durables moddés, bâtons/baguettes RPG Series,
# et tout item ajouté au tag #capitale:adventure_fire_extinguishers.
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:weapon] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:tool] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:max_damage] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand #capitale:adventure_fire_extinguishers unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
