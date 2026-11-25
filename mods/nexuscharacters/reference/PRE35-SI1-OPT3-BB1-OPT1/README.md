# Nexus BB1.OPT1 — travail lié à l’issue #187

Base binaire figée : Nexus BB1 Fabric 1.21.11, SHA-256
`a5f0c4a211a64503e30ffb93a6499a9172b7c22183542b18fc2e1a4cd231b10f`.
Prérequis source : PR #186, commit `4cb9fd89b8bf896b69567a40ad9f9a5845632e6f`.
Ne pas reconstruire depuis la PRE17 encore sur main.

## Changements

- Écriture des surfaces locales directement dans le format natif : mêmes opérations
  flottantes, normales, UV, winding et triangulation ; moins d’objets intermédiaires.
- Découpe six plans avec buffers temporaires primitifs, sans matérialiser les
  sommets intérieurs qui seront jetés.
- Mémoïsation bornée des transformations de listes immuables internes (12 Mio).
- Identités de géométrie canonique : comparaison complète des bits des positions,
  UV, normales, rangs et boîtes. Une couleur seule ne duplique plus le travail local.
  Aucune texture ni identité de personnage n’est échangée.
- Cache local canonique par modèle (8 Mio) et cache des parties nettoyées
  (16 Mio, 32 profils maximum, 4 variantes exactes par partie).
- Invalidation sur changement des cubes, remplacement des formes et rechargement
  des ressources ; pas de quantification de pose, de LOD ni d’animation figée.
- Les assets de la BB1 restent identiques octet pour octet.

## Construction et contrôles

`build.py` applique une transformation ASM contrôlée à trois classes de la BB1,
puis compile les helpers Java 21. Les méthodes de référence sont conservées.
Les ressources de toutes les entrées `assets/` sont comparées au binaire de base.

`kernel_test.py` compare 20 000 polygones et des cas limites : découpe et écriture
comparées bit pour bit à la BB1 (100 016 comparaisons).

`-Dnexus.opt1.verify=true` active les oracles supplémentaires dans le client réel.
Cette instrumentation est réservée aux tests ; les performances sont mesurées
sans ce drapeau et sans figer les poses.

Le banc A/B est `perf_bb1_20261125/run.py` dans le dossier de travail parent.
Le candidat doit être fourni explicitement avec `--jar` et `--expected-sha`.
Chaque client utilise un monde neuf isolé ; aucune connexion au serveur réel.

État : implémentation en cours de validation, pas une promesse de performance.
