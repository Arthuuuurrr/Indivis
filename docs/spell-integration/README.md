# Intégration spells / skills — archive TEST3

Cette section archive le travail réalisé le 22 septembre 2026 autour de l’intégration **CapSkills ↔ Spell Engine ↔ Spell Power ↔ équipements**.

Issue de consolidation : #68.

## Portée

Composants concernés :

- Haute Capitale RPG ;
- Spell Engine ;
- Spell Power ;
- CapSkills ;
- capitale_skills_items ;
- Arsenal ;
- Hazennstuff ;
- AzureLibArmor ;
- Witcher RPG ;
- mods RPG Series utilisés par les abilities.

## Règle de lecture

Les documents distinguent toujours :

1. **validation statique** : structure de JAR/ZIP, JSON, références, graphe, bytecode, invariants ;
2. **validation runtime** : démarrage client/serveur et comportement en jeu.

Une validation statique réussie ne vaut pas validation runtime.

## État de référence

La branche TEST1/TEST2 est abandonnée. La lignée de référence est **TEST3**.

Voir :

- [FINAL_STATE.md](FINAL_STATE.md) — versions finales et décisions encore valides ;
- [PATCH_INVENTORY.md](PATCH_INVENTORY.md) — classes/fichiers réellement modifiés ;
- [VALIDATION.md](VALIDATION.md) — validations statiques et résultats ;
- [RUNTIME_MATRIX.md](RUNTIME_MATRIX.md) — scénarios à vérifier en jeu ;
- [INCIDENTS.md](INCIDENTS.md) — incidents rencontrés et diagnostic ;
- [BINARY_MANIFEST.sha256](BINARY_MANIFEST.sha256) — hashes des artefacts distribués ;
- [archive/](archive/) — rapports historiques bruts, y compris les étapes remplacées.

## Artefacts binaires

Le dépôt Indivis privilégie les **sources, tests et rapports**. Les JAR/ZIP distribuables sont identifiés ici par nom exact et SHA-256 afin de pouvoir vérifier sans ambiguïté les fichiers utilisés en client/serveur.

Le bundle distribué au terme de cette séquence est :

`HAUTE_CAPITALE_FINAL_MODS_RC7B_HAZENN_CRASHFIX.zip`

SHA-256 : `a4a65ac6075ae58cb8cebc2c676ece06f15ea3bf2a9ae70b286c177176a67eed`.
