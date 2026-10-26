# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/dqhc02/0 if score @s CAP_CD_BEER_GC matches 0 run return 1
execute if predicate hc_quetes:migration/state/dqhc02/100_to_ if score @s CAP_CD_BEER_GC matches 0 run return 1
return 0
