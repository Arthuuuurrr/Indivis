# Nexus ARM1 — pièces préparées et contacts animés

Issue #189. Base cumulative : BB1.OPT1 issue de #188, SHA-256
`5017ec3f87d4d72053855341050ada3bd80f262e4238c32d0da1bc982842f5e3`.

## Architecture

- Géométrie locale immuable préparée par apparence/pièce puis réutilisée avec
  les transformations ordinaires du modèle (tête, corps, bras, jambes).
- Hiérarchie de volumes englobants construite une fois sur les faces locales.
  Une séparation CONSERVATRICE dans l’espace MONDE identifie les faces qui ne
  peuvent rencontrer aucune autre partie. Une séparation uniquement dans l’espace
  local ne préserverait pas les subdivisions de la découpe six plans existante.
- Seuls les segments de faces potentiellement en contact sont transformés,
  découpés par l’algorithme OPT1 puis reconvertis en coordonnées locales.
- Une seconde hiérarchie spatiale sur les volumes d’occlusion est construite
  une fois par recette géométrique. Sa topologie est réutilisée et ses boîtes
  mondiales sont actualisées avec les poses. Les feuilles testent les boîtes
  d’origine : aucun volume fusionné ne remplace les volumes dans la découpe.
  Cela évite de traiter comme contacts les vides d’une grande boîte globale.
- Les segments statiques et dynamiques sont assemblés dans l’ordre initial :
  aucun changement d’UV, de winding, de matériau, de couleur ou de topologie attendu.
- Buffers finaux immuables ; aucun buffer de travail mutable n’est exposé au
  rendu différé. Cache borné à 32 Mio estimés / 256 sources, purgé au reload.
  Cache de topologies d’occlusion : 16 Mio estimés / 32 recettes maximum.
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

Test aléatoire : 3 000 cas de 32 polygones ; 6 266 comparaisons, y compris
128 changements de pose avec le MÊME buffer rigide et sans nouveau bake,
128 poses avec 144 volumes d’occlusion et l’invalidation d’une grille modifiée.
Écart maximal observé sur ce test : position 2,98e-7, normale 2,15e-7.

Le profil séparé de la BB1.OPT1 actuelle confirme que les découpes, transformations
et recherches spatiales restent les principaux postes CPU. Les proportions du
profil de la BB1 antérieure ne sont pas réutilisées comme des mesures de l’OPT1.

## Reproduction et portée

Cet instantané est un patch cumulatif du binaire OPT1 indiqué ci-dessus, et non
un remplacement des anciennes sources PRE17 dans le projet Gradle. Il requiert
le workspace de QA existant (`presets_import/runtime`, clients et dépendances
Fabric 1.21.11, banc `perf_bb1_20261125`, liaison NPC). Aucun secret, monde de
production ou profil JVM brut ne fait partie des sources.

1. `python3 armor_cache_20261125/build.py` prépare un nouveau JAR. Ne pas relancer
   cette commande pendant un client de mesure : le binaire est figé pour la QA.
2. `python3 armor_cache_20261125/validate.py --resume` vérifie le noyau, l'audit
   binaire, l'API commune puis cinq clients Minecraft réels. La reprise réutilise
   uniquement les succès avec le même SHA-256.
3. `python3 armor_cache_20261125/measure.py --resume` exécute six clients animés
   A1/B1/B2/A2/A3/B3 successifs. Un résultat final ne doit pas être écrasé ;
   utiliser de nouveaux labels pour un nouveau candidat.
4. `review.py` prépare neuf planches ; `qa/visual-review.json` consigne leur
   inspection réelle. `package.py` exige tous les contrôles du même binaire,
   agrège les mesures et vérifie le JAR contenu dans le ZIP.

Les scripts réutilisent les bancs antérieurs. `fixtures/` conserve les versions
des points instrumentés ; les clients de QA ne sont jamais inclus dans le mod.
Les 126 lignes de géométrie de première personne sont comparées en bits ;
les 411 états mondiaux suivent le contrat topologie/UV/arrondis ci-dessus.

Suivi : [PR #190](https://github.com/Arthuuuurrr/Indivis/pull/190), brouillon
pour revue. Aucun déploiement ni fusion automatique. Rapport livré généré
seulement après l'ensemble des contrôles et des mesures finales.
