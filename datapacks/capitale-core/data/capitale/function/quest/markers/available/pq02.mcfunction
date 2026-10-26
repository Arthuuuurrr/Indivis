# QMARK1 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless score @s CAP_QUETEACTIVE matches 0 unless score @s CAP_QUETEACTIVE matches 4 run return 0
execute if predicate hc_quetes:migration/state/pq01/100 if predicate hc_quetes:migration/state/pq02/0 run return 1
execute if predicate hc_quetes:migration/state/pq01/100 if predicate hc_quetes:migration/state/pq02/10 run return 1
execute if predicate hc_quetes:migration/state/pq01/100 if predicate hc_quetes:migration/state/pq02/1_to_2 run return 1
return 0
