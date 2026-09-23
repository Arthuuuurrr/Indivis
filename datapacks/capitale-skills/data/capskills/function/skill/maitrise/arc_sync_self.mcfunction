# Bonus conditionnel : uniquement tant qu’un arc est tenu.
attribute @s ranged_weapon:haste modifier remove capskills:maitrise_arc
execute if items entity @s weapon.mainhand #capitale:weapon/bows run attribute @s ranged_weapon:haste modifier add capskills:maitrise_arc 0.08 add_multiplied_base
