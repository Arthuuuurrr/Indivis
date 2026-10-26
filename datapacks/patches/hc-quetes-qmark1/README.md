> Archive QMARK1. Pour l’installation actuelle, voir [HF12 / QMARK2](../../../docs/quests/pq01-dirigeable-r3/HF12/README.md).

# Source du raccordement QMARK1

Ce dossier montre la seule fonction historique modifiée dans hc_quetes : un appel est ajouté à la fin de runtime/second, après préparation du contexte Nexus. Il s'agit d'une source de revue, pas d'un troisième datapack à installer.

La reconstruction de livraison est `python3 work/pq01_full/build_quest_markers.py` : elle produit les deux datapacks complets à partir de HF9/HF4 et vérifie que le JAR HF11 est identique. Le catalogue, le test Fabric et les limites sont dans docs/quests/core-quest-markers-qmark1/.
