# Nexus PRE35 SI1 OPT3

Issue #182 ; branche `fix/nexus-posed-surface-cost` ; PR brouillon #183.
Pas de fusion dans main ni déploiement de production.

## Résultat retenu

JAR : `NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3.jar`.
SHA-256 : `de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b`.
Base OPT2 : `7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116`.

Combinaison retenue après ablation : volumes/cellules exacts par membre,
énumération directe de l’index, listes intérieures non échappantes recyclées
dans le découpeur. Cache de volumes borné à six entrées par membre et huit Mio
estimés par workspace réentrant, éviction LRU ; origine conservée pour les rangs
égaux. Invalidation par identité de base/template/Shape et matrices exactes.
Les grilles produites servent exclusivement au nettoyage en lecture seule.

Le découpeur original OPT2 reste `intersectingOPT2` et la méthode de préparation
originale reste `applyOPT2`. Les oracles se déclenchent seulement avec
`-Dnexus.opt1.verify=true`, jamais pendant les mesures. Aucun changement d’EPS,
d’UV, de coordonnée, d’asset, de LOD ou d’animation.

## Reproduction

Le dossier de travail est `opt3/`, à côté du dossier `opt2/` de la référence
précédente. Restaurer ses scripts/runtime/harness avec les procédures de
`PRE35-SI1-OPT2` puis placer le JAR OPT2 vérifié sous
`opt2/out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar`.
Les binaires tiers et les mondes temporaires ne sont pas stockés dans Git.

1. `python3 opt3/build.py` — défaut : combinaison retenue, build déterministe.
2. `python3 opt3/run_exact.py` — transformation, extérieur, index, volumes,
   découpeur compact (6192 cas dont EPS adjacent) et propriété des sorties.
3. `python3 opt3/audit.py` — 33 méthodes d’origine identiques hors debug/frames ;
   seuls le constructeur d’index et le préfixe d’invalidation de Grid.add sont
   traités séparément.
4. Lancer OPT2 via `run_client.py` avec label `opt2-reference-perf`,
   `OPT_PERF=true`, `NEXUS_VERIFY=false`, puis `python3 opt3/finish_tests.py`.
   Ne pas superposer deux clients ou un oracle aux chronométrages.
5. `python3 opt3/compare.py`, `python3 opt3/verify_release.py`, puis
   `python3 opt3/package_release.py`.

Le harness NPC contient une seule addition de diagnostic à `OptCost.summary`
pour relever les compteurs OPT3 par réflexion. Aucune modification du bridge.
La copie complète de ce fichier est conservée dans `fixtures/npc/OptCost.java`.

Ablations : `OPT3_COMPACT=false` (volumes seuls), ou
`OPT3_VOLUMES=false` (découpeur seul). Les variables `OPT3_FILENAME` et
`OPT3_BUILD_RECORD` permettent de ne pas écraser le candidat validé.
Le premier cache deux Mio/vidage complet n’est pas retenu ; son build et ses
mesures sont archivés, mais pas son binaire expérimental de 27 Mio.

## Vérifications effectuées

Voir `VERIFICATIONS_NEXUS_OPT3.md`, `qa/release-audit.json`, `qa/exact.json`,
`qa/bytecode-audit.txt`, toutes les lignes de `qa/comparison.csv` et les fichiers
`qa/comparison-opt3-*.json`. Tous les enregistrements de la release chargent le
même SHA ci-dessus. Une reconstruction finale reproduit ces octets.

Deux processus de mesure de la combinaison donnent un gain sur les douze cas
animés face au processus OPT2 de référence ; pas d’amélioration uniforme
revendiquée sur les six cas statiques. Exemples de la série finale : PNJ3 tête
13,667 → 11,429 ms ; PNJ8 marche 45,459 → 42,286 ms ; joueurs8 marche
47,020 → 43,340 ms. Allocations des poses animées : baisse d’environ 10–17 %.
Il s’agit de préparation native CPU, pas des FPS du pack.

Limites explicites : rendu logiciel et pack de test réduit ; pas de nouvelle
preuve pour l’occlusion dans le pack réel, les sorts/caméra de dialogue ou une
authentification Mojang. Les avertissements de vault hors ligne antérieurs
restent présents ; persistance joueurs non déclarée validée. Les vrais PNJ,
UUID/recettes/tags sont contrôlés par fermeture et réouverture du monde.
