# CapSkills 0.9.67 EXP — synchronisation MANUELLE des familles d’armes tenues
# Tags joueurs informatifs seulement ; non appelée automatiquement pour éviter une charge serveur inutile.
tag @s remove capskills.weapon.sword_classic
tag @s remove capskills.weapon.short_impact
tag @s remove capskills.weapon.onehand_axe
tag @s remove capskills.weapon.spear_short
tag @s remove capskills.weapon.lance_long
tag @s remove capskills.weapon.twohand_heavy
tag @s remove capskills.weapon.polearm_heavy
tag @s remove capskills.weapon.assassin_light
tag @s remove capskills.weapon.staff
tag @s remove capskills.weapon.magic_focus
tag @s remove capskills.weapon.ranged_bow
tag @s remove capskills.weapon.ranged_crossbow
tag @s remove capskills.weapon.firearm_revolver
tag @s remove capskills.weapon.thrown_light
tag @s remove capskills.weapon.shield
tag @s remove capskills.weapon.melee_compatible
execute if items entity @s weapon.mainhand #capskills:weapon/sword_classic run tag @s add capskills.weapon.sword_classic
execute if items entity @s weapon.mainhand #capskills:weapon/short_impact run tag @s add capskills.weapon.short_impact
execute if items entity @s weapon.mainhand #capskills:weapon/onehand_axe run tag @s add capskills.weapon.onehand_axe
execute if items entity @s weapon.mainhand #capskills:weapon/spear_short run tag @s add capskills.weapon.spear_short
execute if items entity @s weapon.mainhand #capskills:weapon/lance_long run tag @s add capskills.weapon.lance_long
execute if items entity @s weapon.mainhand #capskills:weapon/twohand_heavy run tag @s add capskills.weapon.twohand_heavy
execute if items entity @s weapon.mainhand #capskills:weapon/polearm_heavy run tag @s add capskills.weapon.polearm_heavy
execute if items entity @s weapon.mainhand #capskills:weapon/assassin_light run tag @s add capskills.weapon.assassin_light
execute if items entity @s weapon.mainhand #capskills:weapon/staff run tag @s add capskills.weapon.staff
execute if items entity @s weapon.mainhand #capskills:weapon/magic_focus run tag @s add capskills.weapon.magic_focus
execute if items entity @s weapon.mainhand #capskills:weapon/ranged_bow run tag @s add capskills.weapon.ranged_bow
execute if items entity @s weapon.mainhand #capskills:weapon/ranged_crossbow run tag @s add capskills.weapon.ranged_crossbow
execute if items entity @s weapon.mainhand #capskills:weapon/firearm_revolver run tag @s add capskills.weapon.firearm_revolver
execute if items entity @s weapon.mainhand #capskills:weapon/thrown_light run tag @s add capskills.weapon.thrown_light
execute if items entity @s weapon.mainhand #capskills:weapon/shield run tag @s add capskills.weapon.shield
execute if items entity @s weapon.mainhand #capskills:weapon/melee_compatible run tag @s add capskills.weapon.melee_compatible
execute if items entity @s weapon.offhand #capskills:weapon/shield run tag @s add capskills.weapon.shield
