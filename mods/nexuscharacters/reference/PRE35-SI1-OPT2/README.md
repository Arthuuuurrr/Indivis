# Nexus PRE35 SI1 OPT2 — livrable vérifié

Issue #182, branche `fix/nexus-posed-surface-cost`, PR #183 en brouillon pour revue.
Aucune fusion dans main, aucun déploiement en production.

## Version retenue

Version interne : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT2`.
SHA-256 : `7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116`.
Base OPT1 : `bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11`.

OPT1 est conservé. OPT2 dimensionne les ArrayList selon les nombres de faces et
sommets pendant les transformations. Vertex.transform, normales, UV et ordre des
opérations sont inchangés. La transformation originale reste disponible comme
oracle transformOPT1. Seuls fabric.mod.json et SurfaceGeometry.class changent ;
ExactTransformSupport est ajouté. Les 4966 assets, le clipping OPT1, réseau,
persistance, menus et liaison PNJ sont inchangés.

Les caches de classes/requêtes et les listes de fragments recyclées ont été
retirés après les mesures CPU. Les prototypes conservés dans experiments/ ne
sont PAS compilés dans la livraison. Leurs chiffres intermédiaires ne doivent
pas être attribués au JAR final.

## Preuves finales

Voir VERIFICATIONS_NEXUS_OPT2.md et qa/ pour les résultats bruts et les limites :
compilation reproductible Java 21, 2400 cas de transformation, 10368 cas de faces
extérieures, 5400 cas de découpe, oracle exact dans le client réel, 216 états
PNJ/joueurs identiques entre les trois versions, 466 états supplémentaires
identiques à OPT1 et 23 menus identiques pixel par pixel. Nouveaux assets 930,
corps normal/slim, réglages modifiés à chaud et réouverture d'un monde de dix
vrais EasyNPC contrôlés. Les vues de face/dos sont inspectées.

Le gain OPT2 additionnel est modeste, surtout sur les allocations. Ne pas lui
attribuer seul les gains cumulatifs OPT1 face à SI1. Les variations défavorables,
notamment sur les joueurs immobiles, ne sont pas masquées. CPU de préparation,
pas FPS du pack. Sorts, caméra de dialogue et Mojang authentifié ne sont pas
reproduits dans cette session. Les avertissements de vault du compte de test
hors ligne restent présents ; aucune validation de ce coffre n'est prétendue.

## Reproduction

Le Xvfb est uniquement local au dossier de test. Les dépendances tierces ne sont
pas redistribuées dans les sources. Configurer NEXUS_OPT1_JAR vers la base exacte ;
reprendre les JAR d'origine de liaison PREVIEW1 et du HUD OPT1 dans inputs/.

```text
python3 bootstrap.py
python3 setup_display.py
python3 prepare_tests.py
python3 build.py
python3 run_exact.py
python3 finish_tests.py
python3 compare.py
python3 verify_release.py
python3 package_release.py
```

Pour compare.py, exécuter aussi les références opt1-perf, pre35-perf et
menus-baseline avec leurs JAR respectifs. Chaque preuve conserve le hash des
véritables octets chargés. Les fichiers de harnais ne sont jamais à installer
dans le modpack de production.

