# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/pq01/0 unless score @s hcm_pq1_active matches 1.. run return 1
return 0
