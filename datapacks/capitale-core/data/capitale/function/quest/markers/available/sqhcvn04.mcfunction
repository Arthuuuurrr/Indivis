# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless score @s CAP_QUETEACTIVE matches 0 run return 0
execute if predicate hc_quetes:migration/state/sqhcvn03/100 if predicate hc_quetes:migration/state/sqhcvn04/0 run return 1
return 0
