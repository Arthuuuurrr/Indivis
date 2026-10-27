# Nexus PRE35 SI1 OPT2 — brouillon, non livré

Issue #182 ; suite de la branche `fix/nexus-posed-surface-cost` et de la PR #183.
Base exacte OPT1 : `bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11`.

## Changements implémentés

- Cache de requêtes conservant séparément les sept entiers complets, sans
  allocation d'une clé objet par recherche. Croissance paresseuse, 4096 réponses
  au maximum, même durée de vie que l'index de grille OPT1. Aucun empaquetage
  des coordonnées : pas d'alias ajouté aux clés 21 bits historiques.
- Curseur de bornes réutilisé pour les polygones sources et deux listes de
  fragments alternées à l'intérieur d'un appel. Les Polygon retournés ne sont
  pas mutés ; les objets retenus par un modèle/cache ne sont pas recyclés.
- Vecteur normal de travail réutilisé dans le test de faces extérieures, avec
  les mêmes appels JOML, le même ordre de calcul et les mêmes seuils.
- Trois appels Class.forName littéraux redirigés dans GenericSkinLayerSupport.player
  et LowerJointSupport.apply. Le loader Nexus et la barrière d'initialisation
  sont préservés, y compris en cas d'initialisation récursive/concurrente.
  Les échecs ne sont pas mis en cache et les paramètres Skin Layers ne sont
  pas figés : leurs lectures de champs d'origine restent en place.

Aucun LOD, saut d'animation, arrondissement de pose, changement d'EPS ou
modification des assets. Le cache par partie et son invalidation OPT1 sont inchangés.
Les classes réseau, persistance, UI et le mod compagnon PNJ restent inchangés.

## Contrôles réellement terminés

- Compilation Java Temurin 21.0.13+9.
- 516151 assertions sur clés complètes, collisions, croissance, limite de cache,
  réponses vides, identité des résultats, classes absentes, accès restreints,
  initialisation concurrente et récursive.
- 10368 tests de faces extérieures/coplanarité/seuils/transformation non uniforme.
- 5400 cas de découpe comparés au corps PRE35 conservé dans cleanPRE35 :
  454381 polygones comparés, coordonnées/normales/UV/topologie/rang/ordre exacts.
  Les 774 cas de rangs ambigus conservent le chemin PRE35.
- 4966 entrées d'assets identiques octet pour octet à OPT1.

Les résultats bruts sont dans `qa/exact.json` et `qa/build.json`. Le hash du
candidat compilé y identifie exactement les octets testés.

## Blocage et contrôles encore nécessaires

Le client Minecraft et les dépendances de compilation ont été restaurés, mais
aucun nouveau client graphique n'a été exécuté pour OPT2. Xvfb est absent ;
la tentative de préparation système via apt a échoué avec Operation not permitted
sur setgroups/setegid/seteuid. Aucune relance privilégiée ni contournement effectué.

Une direction de l'utilisateur est attendue avant de préparer une alternative
d'affichage locale au dossier de test. Ne pas publier ce JAR comme version validée.

Restent : mesures CPU/allocations appariées OPT1/OPT2, puis contrôles en jeu avec
joueurs et vrais EasyNPC normal/slim, menus, première personne, changements de
configuration Skin Layers, ressources/états et nouveaux assets. Toute régression
de performance doit conduire à retirer ou revoir le changement concerné.

Aucun gain CPU/FPS nouveau n'est encore revendiqué. Les anciens chiffres OPT1
ne valident pas OPT2. Aucun déploiement de production et aucune fusion dans main.

## Reproduction des tests isolés

Java 21 est placé dans `runtime/jdk`. L'archive officielle utilisée est
OpenJDK21U-jdk_x64_linux_hotspot_21.0.13_9.tar.gz, SHA-256
`d7f5d5cf865a2902b70ff549b97586a7d3f234e016f041ab447ea740c7d66d1c`.
`bootstrap.py` récupère les distributions et métadonnées primaires du client
1.21.11 et des dépendances ; elles ne sont pas redistribuées dans ce dossier.

Configurer `NEXUS_OPT1_JAR` vers le JAR OPT1 exact, puis lancer :

```text
python3 bootstrap.py
python3 build.py
python3 run_exact.py
```

Le JAR compilé de travail est sous `out/`. Il ne constitue pas un livrable de
production tant que les contrôles et les mesures restants ne sont pas terminés.
