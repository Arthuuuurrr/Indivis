# Nexus ARM1 — pièces préparées et contacts animés

Issue #189. Base cumulative : BB1.OPT1 issue de #188, SHA-256
`5017ec3f87d4d72053855341050ada3bd80f262e4238c32d0da1bc982842f5e3`.

## Architecture du prototype

- Géométrie locale immuable préparée par apparence/pièce puis réutilisée avec
  les transformations ordinaires du modèle (tête, corps, bras, jambes).
- Hiérarchie de volumes englobants construite une fois sur les faces locales.
  Une séparation CONSERVATRICE dans l’espace MONDE identifie les faces qui ne
  peuvent rencontrer aucune autre partie. Une séparation uniquement dans l’espace
  local ne préserverait pas les subdivisions de la découpe six plans existante.
- Seuls les segments de faces potentiellement en contact sont transformés,
  découpés par l’algorithme OPT1 puis reconvertis en coordonnées locales.
- Les segments statiques et dynamiques sont assemblés dans l’ordre initial :
  aucun changement d’UV, de winding, de matériau, de couleur ou de topologie attendu.
- Buffers finaux immuables ; aucun buffer de travail mutable n’est exposé au
  rendu différé. Cache borné à 32 Mio estimés / 256 sources, purgé au reload.
- Invalidation d’apparence conservée via les sources natives existantes. Les
  recettes trop grandes ou transformations non prises en charge passent par
  l’algorithme OPT1 intégral ; aucun asset n’est désactivé.
- La première personne dédiée et tous les assets/UI restent inchangés.

## Précision et vérification

Les faces rigides ne subissent plus l’aller-retour monde/inverse à chaque pose.
Elles peuvent donc différer de quelques unités d’arrondi flottant de l’ancien
buffer local, tout en ayant la même géométrie. Ce n’est PAS un mode de poses
quantifiées : chaque pose reste animée normalement.

`nexus.armor.verify=true` ou `nexus.opt1.verify=true` compare chaque préparation
au chemin OPT1 conservé : même nombre/ordre des quads et mêmes bits UV ; positions
et normales contrôlées à `2e-6 + 8 ulp` par composante. Les segments de contact
utilisent le calcul de référence sans approximation géométrique nouvelle.

Test aléatoire : 3 000 cas de 32 polygones ; 6 137 comparaisons, y compris
128 changements de pose avec le MÊME buffer rigide et sans nouveau bake.
Écart maximal observé sur ce test : position 2,98e-7, normale 2,15e-7.

Le profil séparé de la BB1.OPT1 actuelle confirme que les découpes, transformations
et recherches spatiales restent les principaux postes CPU. Les proportions du
profil de la BB1 antérieure ne sont pas réutilisées comme des mesures de l’OPT1.

État : prototype en cours de vérification client et de comparaison A/B.
Pas de JAR de production validé à ce stade, pas de déploiement automatique.
