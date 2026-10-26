# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute if predicate hc_quetes:migration/state/sqhcso01/0 run return 1
execute if predicate hc_quetes:migration/state/sqhcso01/1_to_2 run return 1
return 0
