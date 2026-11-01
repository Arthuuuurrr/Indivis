# PQ02 R1 HF9 — Le registre du Port

HF9 conserve la quête HF8 opérationnelle et ajoute des pauses de lore aux repères D/H, une navigation 50 % plus rapide en consigne (0,8 → 1,2), l’enchaînement immédiat des points ordinaires et des profils b10 centrés lors de l’entretien avec le magistrat. Les conversations facultatives, sauvegardes par personnage, récompenses et les 111 identifiants narratifs restent compatibles.

Depuis **HF8 + hc_quetes Fusion C1**, remplacer **seulement le JAR de quêtes** sur serveur et clients. Les deux datapacks sont strictement identiques. Toutes les commandes et liaisons restent valables. Voir `INSTALLATION.md`, `COMMANDES_PQ02.md` et `CORRECTIF_PQ02_HF9.md`.

Les 50 Martins restent versés pendant F06 avant la réponse ; aucun retour n’est requis pour les récupérer. Les modifications de Clément restent dans le datapack Fusion C1. Les autres quêtes, PQ01 et les mixins existants sont conservés.

## Reproduction HF9

Prérequis : JAR HF8 exact dans `output/` (SHA-256 dans `reports/hf9_build.json`), core HF8 dans `output/`, archive Fusion C1 dans `hf9_inputs/`, JAR tiers autorisés dans `upload/` et `dependencies/`, et les outils C1 voisins sous `tools/hc_quetes_merge_c1/` dans GitHub. Les dépendances ne sont pas redistribuées. Les archives et sources historiques HF8 restent la base ; ne pas relancer `build.py` pour produire HF9.

```sh
python3 tools/pq02/prepare_runtime_hf9.py
python3 tools/pq02/bootstrap_hf9.py
python3 tools/pq02/build_hf9.py
python3 tools/pq02/verify_hf9.py
python3 tools/pq02/run_hf9.py
python3 tools/pq02/run_hf9_protocol.py
python3 tools/pq02/package_hf9.py
```

`run_hf9.py` part d’un monde de test neuf pour chaque exécution et réemploie la fixture C1. Les connexions et sélections Nexus y sont synthétiques ; les fonctions, sessions b10 et fichiers de profils sont réels. Aucun client graphique ni modpack complet de production n’est déclaré testé.

## Historique HF8

# Livraison HF8

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
