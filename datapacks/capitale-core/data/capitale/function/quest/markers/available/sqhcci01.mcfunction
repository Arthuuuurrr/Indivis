# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/sqhcci01/0 if score @s CAP_RANGSOCIAL matches 20..29 run return 1
return 0
