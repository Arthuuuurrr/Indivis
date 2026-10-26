# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless score @s CAP_QUETEACTIVE matches 0 unless score @s CAP_QUETEACTIVE matches 5 run return 0
execute if predicate hc_quetes:migration/state/pq03/100_to_ if predicate hc_quetes:migration/state/pq04/0 run return 1
execute if predicate hc_quetes:migration/state/pq03/100_to_ if predicate hc_quetes:migration/state/pq04/1 run return 1
execute if predicate hc_quetes:migration/state/pq03/100_to_ if predicate hc_quetes:migration/state/pq04/10 run return 1
return 0
