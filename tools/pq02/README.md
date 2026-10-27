# PQ02 R1 — Le registre du Port

Implémentation du kit Work v1.2 et du script PQ02 v1.0, sur copies des deux archives de production fournies. Cible : Fabric 1.21.11, Java 21. Aucun autre JAR existant n’est reconstruit ; `indivis-pq02` est un mod compagnon serveur et clients.

Les procédures de joueur et d’administrateur sont dans `INSTALLATION.md`. L’audit, les limites et les preuves figurent dans `RAPPORT.md` et `reports/`. Dans GitHub, ces documents sont aussi dans `docs/quetes/PQ02_R1/` ; `patches/pq02-r1/` contient les seules ressources modifiées. Les anciens répertoires de datapacks du dépôt ne sont pas substitués à l’archive serveur fournie.

## Reproduire

Placer ce dossier sous `<projet>/work/pq02/` (ou `<projet>/tools/pq02/`) et les originaux sous `<projet>/upload/`, avec les noms attendus par `build.py`. Le fichier `input_manifest.json` identifie les originaux. Garder le script narratif fourni sous `kit/Indivis_PQ02_Script_Consolide_v1_0.md`.

Le banc attend aussi EasyNPC **7.16.0 pour Fabric 1.21.11**, dans `dependencies/`, sous le nom fourni `easy_npc-fabric-1.21.11-7.16.0(1).jar` ou un autre nom `.jar` si c’est la seule dépendance de ce dossier. Les JAR d’origine du journal HF12 et de Dialogue b10 sont lus depuis `upload/`.

```sh
python work/pq02/prepare_runtime.py
python work/pq02/bootstrap_runtime.py
python work/pq02/build.py
python work/pq02/test_static.py
python work/pq02/run_native.py
python work/pq02/package_delivery.py
```

Adapter le préfixe `work/` en `tools/` si nécessaire. Java 21 et le serveur officiel, Fabric Loader/API et les bibliothèques sont préparés sous `/tmp/indivis_pq02_runtime`. Les fonctions tests et les connexions synthétiques restent dans un monde jetable, jamais dans le core livré. Les journaux et résultats sont dans `reports/` ; les archives finales dans `delivery/`.

Le banc ne lance pas de client graphique et n’utilise pas la carte Indivis. Les comptes/connexions, le sélecteur Nexus et les intégrations économiques externes y sont explicitement simulés. Les règles PRE35, les classes HF12, le serveur Minecraft, les entités/navigation EasyNPC, les fonctions testées, les trackers de visibilité et les sessions/captures b10 sont réels. Les annotations d’environnement client sont retirées seulement de la copie b10 du banc ; les fichiers de production ne contiennent aucune doublure.

## Organisation

`model.py` extrait les citations et construit les 111 nœuds. `build.py` vérifie les empreintes des ZIP originaux, applique les changements limités et compile le compagnon contre les véritables dépendances. `test_static.py` contrôle les différences exactes, les graphes et exécute les règles Java. `run_native.py` construit et démarre le serveur de test. `package_delivery.py` refuse d’assembler une livraison sans résultats PASS et sans correspondance de l’empreinte du mod testé.

Les quatre mixins de production sont limités à la visibilité des acteurs PQ02, leur physique, leurs cibles de waypoint et la fermeture de leur session Dialogue. PQ01 reste inchangé hors des 12 fichiers du seul choix 6 de Hugues. MQ03 reçoit uniquement la priorité manquante du capitaine au quai, identique à celle du capitaine au large.
