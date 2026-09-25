# capitale_core 1.5.6-RC9AT — interactions cassables en mode Aventure.
# Les objets avec un can_break personnalisé ne sont jamais écrasés.
# Migration ciblée : seul le can_break exact historique fire + soul_fire est étendu.
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:can_break={blocks:["minecraft:fire","minecraft:soul_fire"]}] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish

# Compatibilité : armes/outils vanilla, objets durables moddés, bâtons/baguettes RPG Series,
# et tout item ajouté au tag #capitale:adventure_fire_extinguishers.
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:weapon] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:tool] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand *[minecraft:max_damage] unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
execute as @a[gamemode=adventure] if items entity @s weapon.mainhand #capitale:adventure_fire_extinguishers unless items entity @s weapon.mainhand *[minecraft:can_break] run item modify entity @s weapon.mainhand capitale:adventure/fire_extinguish
