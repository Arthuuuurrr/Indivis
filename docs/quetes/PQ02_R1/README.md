# PQ02 R1 HF8 — Le registre du Port

PQ02 est intégrée au seul mod de quêtes HF12 pour Fabric 1.21.11, serveur et clients. La version HF8 applique la précision utilisateur du 1er novembre : **50 Martins personnels versés systématiquement pendant F06**, avant la réponse ; curiosité/embarras neutres, reconnaissance +1/+1, réponse sèche −1/−1. Aucun refus/report ni retour pour récupérer la bourse.

Le script fourni v1.0 reste intact dans `kit/`. `adapt_script_hf8.py` produit une version de travail v1.1 de 691 lignes et le relevé exact des adaptations. Tous les autres blocs narratifs et les 111 identifiants sont préservés. Les deux anciens nœuds de récupération deviennent des alias compatibles avec les sauvegardes.

`INSTALLATION.md` indique les trois fichiers à remplacer : JAR sur serveur/clients, core et datapack du journal sur serveur. Les commandes et repères de `COMMANDES_PQ02.md` restent valables. `RAPPORT.md` et `reports/hf8.json` relient les vérifications aux empreintes exactes des fichiers livrés.

## Reproduction

Disposer des originaux identifiés dans `input_manifest.json` sous `upload/`, des dépendances requises dans `dependencies/`, et du manifeste historique gelé `hf8_baseline.json` utilisé pour la comparaison. Les JAR de tiers ne sont pas redistribués.

```sh
python3 work/pq02/prepare_runtime.py
python3 work/pq02/bootstrap_runtime.py
python3 work/pq02/adapt_script_hf8.py
python3 work/pq02/build.py
python3 work/pq02/merge_journal.py
python3 work/pq02/test_static.py
PQ02_INTEGRATED=1 PQ02_FULL_SYSTEM=1 python3 work/pq02/run_native.py
PQ02_INTEGRATED=1 python3 work/pq02/run_native.py
PQ02_INTEGRATED=1 PQ02_PROFILE_CHECKPOINT=guard python3 work/pq02/run_native.py
PQ02_INTEGRATED=1 PQ02_HANDOFF=guard python3 work/pq02/run_native.py
python3 work/pq02/run_protocol.py
python3 work/pq02/test_merged_boot.py
python3 work/pq02/audit_prologue.py
python3 work/pq02/package_hf8.py
```

Adapter `work/` à `tools/` dans le dépôt. Lancer les modes natifs **séquentiellement** : ils partagent un port et un répertoire de compilation. Le mode `PQ02_FULL_SYSTEM=1` conserve les JAR de quêtes et Dialogue intacts et active leurs initialiseurs serveur, ConfigUI et le vrai tick core ; son addon de test reste séparé. Les autres modes isolent certaines entrées pour explorer toutes les branches. Aucun substitut de ces bancs n’entre dans les fichiers livrés.

Les rapports HF1–HF7 et l’audit du 31 octobre sont historiques : ils validaient l’ancienne règle de don refusable. Les preuves courantes HF8 se trouvent dans `reports/hf8.json` et les fichiers référencés par ce manifeste. Aucun essai graphique ni mesure de FPS sur la carte Indivis n’est revendiqué.
