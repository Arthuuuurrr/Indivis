# Reconstruire

Python 3 et Java 17+ pour le banc de politique Nexus. Remettre dans `sources/upload/` les deux archives d'entrée, avec leurs noms et SHA-256 exacts listés dans `audit/diffs.json`. Le prompt validé est inclus. Les JAR/données du serveur ne sont pas remplacés par des copies anciennes.

Depuis `sources/` :

```sh
python3 work/pq01_rebuild/build.py
python3 work/pq01_rebuild/validate.py
```

Le banc `tests/NexusPolicyBench.java` utilise la classe `net/tompsen/nexuscharacters/V2QuestPolicy.class` extraite sans modification de `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar` (SHA-256 454bd4206b50737bf386d65eb09f4f0e3ad1294a1fd4e6df426f9fe205cb31d0), placée avec son chemin de package dans `nexus_classes`. Compiler le banc avec ce dossier dans le classpath, puis l'exécuter. Aucun stub Minecraft ni classe Nexus modifiée n'est embarqué dans les datapacks.
