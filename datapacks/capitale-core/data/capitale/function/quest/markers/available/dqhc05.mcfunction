# QMARK1 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless predicate hc_quetes:migration/state/dqhc05/20 unless score @s hcd_dqhc05 matches 1.. run return 1
return 0
