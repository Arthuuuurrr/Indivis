# Indivis — PQ03 R1 HF1

Implémentation du dossier `PQ03_finalisation_Work_2026-11-07.md`, sur les trois composants installés identifiés dans `reports/build.json`. Les sources finales sont dans `src/`. Le générateur historique `seed_runtime.py` a servi à amorcer le module ; il ne doit pas être réexécuté sur ces sources finales.

HF1 applique les effets de réputation au choix avec un bandeau privé et une migration des décisions R1 en attente. Voir `docs/quetes/PQ03_R1/RAPPORT_PQ03_R1_HF1.md` dans le dépôt. Les parcours et les dialogues de R1 restent identiques.

## Contenu

- `model.py` extrait mot pour mot les répliques et choix du scénario et construit les 57 blocs.
- `build.py` copie les archives épinglées, compile uniquement l’extension Java 21 et produit les deux datapacks et le JAR fusionné.
- `verify.py` contrôle les changements autorisés, les fichiers préservés, le graphe et les contrats Java avec la politique Nexus réelle.
- `run_native.py` réutilise la fixture PQ02 et C1 du dépôt, puis exécute les deux parcours PQ03 et un second processus Fabric pour le redémarrage.
- `run_protocol.py` teste le protocole PQ03 ; `PQ03_PROTOCOL_MODE=pq02` teste PQ02 avec le listener PQ03 chargé.
- `tests/`, `reports/` et `docs/` : fixtures, preuves et installation. Aucun substitut de test n’entre dans les composants distribués.

## Reproduction

Exécuter les scripts depuis la racine du workspace/dépôt avec Python 3 et Java 21. La disposition attendue est `tools/pq03` et `tools/pq02` dans le dépôt, ou `work/pq03` et `work/pq02` dans ce workspace.

1. Fournir les archives originales dans le dossier racine `upload/`, aux chemins `BASE` de `build.py`, avec les SHA-256 indiqués dans le manifeste. Fournir le JAR **PQ02 HF9 antérieur à R1** dans `pq03/baseline_mod/`. Le build refuse une autre empreinte. Ne pas substituer le JAR PQ03 à cette base.
2. Extraire les deux datapacks de base dans `pq03/baseline_core/` et `pq03/baseline_quests/`. `model.py` lit les salutations postquête existantes de cette copie du core ; il ne les invente pas.
3. Pour la compilation, conserver `pq02/runtime_hf9/` avec JDK 21.0.13, les bibliothèques du serveur Minecraft 1.21.11 et le serveur intermédiaire Fabric Loader 0.19.5. Fournir le mod Dialogue b10 sous `upload/`. Pour les contrôles de persistance, fournir NexusCharacters PRE35 SI1 OPT3, SHA indiqué dans le rapport.
4. Pour les tests natifs, fournir les ressources de test PQ01/PQ02 du dépôt, les dépendances EasyNPC + Config UI 7.16, Dialogue b10, Nexus OPT3 et Fabric API 0.141.6. La fixture C1 se trouve dans `tools/hc_quetes_merge_c1/` dans le dépôt. Les bibliothèques binaires et le serveur Minecraft restent des entrées de test locales ; ils ne sont pas copiés dans GitHub ou dans les fichiers à installer.
5. Lancer dans cet ordre, en remplaçant `tools` par `work` selon la disposition :

   ```bash
   python3 tools/pq03/build.py
   python3 tools/pq03/verify.py
   python3 tools/pq03/run_native.py
   python3 tools/pq03/run_protocol.py
   PQ03_PROTOCOL_MODE=pq02 python3 tools/pq03/run_protocol.py
   ```

Les tests natifs créent un monde de fixture neuf, puis redémarrent ce même monde dans un second processus. Ils ne lisent pas le monde utilisateur. Les deux modes du protocole partagent leurs classes temporaires : les lancer l’un après l’autre. Le serveur de fixture utilise le port 25579.

Le code n’est pas une reconstruction complète des autres mods. Le JAR antérieur est copié puis enrichi afin de préserver ses 255 entrées inchangées. Le manifeste détaille les trois entrées existantes modifiées. Les validations natives nécessitent ces dépendances locales ; `verify.py` ne remplace pas leur exécution.

## Entrées supplémentaires pour vérifier HF1

Conserver les trois sorties **R1 antérieures à HF1** sous `pq03_fix_20261108/baseline_r1/output/`, à côté de `pq03/`. `verify.py` et `finalize_hf1.py` utilisent cette copie pour mesurer la portée exacte du correctif. Fournir Nexus OPT3 sous `pq03_fix_20261108/downloads/` pour le contrôle de sa politique réelle. Ces fichiers ne font pas partie des éléments à installer sur le serveur. Les sorties R1 sont identifiées par les empreintes du rapport historique ; ne pas substituer les sorties HF1 à cette copie.
