# Préfixe automatique via Custom Name.
# Les préfixes ne contiennent pas d’espace pour éviter que le mod conserve des guillemets.
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches ..9 run name other prefix @s &8[Étranger]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 10..19 run name other prefix @s &7[Visiteur]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 20..29 run name other prefix @s &f[Résident]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 30..39 run name other prefix @s &a[Citoyen]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 40..49 run name other prefix @s &2[Marchand]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 50..59 run name other prefix @s &e[Notable]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 60..69 run name other prefix @s &e[Noble-mineur]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 70..79 run name other prefix @s &e[Noble]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 80..89 run name other prefix @s &b[Agent]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 90..99 run name other prefix @s &5[Dignitaire]
execute if score @s CAP_PREFIXMODE matches 0 if score @s CAP_RANGSOCIAL matches 100.. run name other prefix @s &6[Empereur]
