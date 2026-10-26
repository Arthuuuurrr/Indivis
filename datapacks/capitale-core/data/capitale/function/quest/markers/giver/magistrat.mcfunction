# QMARK1 : visibilité personnelle des offres du core.
execute if function capitale:quest/markers/available/pq05 run return run function capitale:quest/markers/locate/magistrat with storage hc_quetes:session current
execute if function capitale:quest/markers/available/sqhcci01 run return run function capitale:quest/markers/locate/magistrat with storage hc_quetes:session current
return 0
