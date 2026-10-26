# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless score @s CAP_QUETEACTIVE matches 0 unless score @s CAP_QUETEACTIVE matches 6 run return 0
execute if predicate hc_quetes:migration/state/pq04/100_to_ if predicate hc_quetes:migration/state/pq05/0 if score @s CAP_RANGSOCIAL matches ..19 run return 1
execute if predicate hc_quetes:migration/state/pq04/100_to_ if predicate hc_quetes:migration/state/pq05/1 if score @s CAP_RANGSOCIAL matches ..19 run return 1
execute if predicate hc_quetes:migration/state/pq04/100_to_ if predicate hc_quetes:migration/state/pq05/10 if score @s CAP_RANGSOCIAL matches ..19 run return 1
return 0
