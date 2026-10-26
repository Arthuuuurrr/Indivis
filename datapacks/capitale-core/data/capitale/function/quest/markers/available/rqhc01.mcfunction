# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/rqhc01/0 unless score @s hcd_rqhc01 matches 1.. run return 1
return 0
