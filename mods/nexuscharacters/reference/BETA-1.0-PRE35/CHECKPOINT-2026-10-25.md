# Historique de la première PRE35 — remplacée après reconstruction

Le nettoyage de l’environnement a finalement supprimé le premier binaire décrit ci-dessous. Une PRE35 SI1 reconstruite a depuis été compilée, entièrement revérifiée et enregistrée comme livrable. Le rapport courant est `PRE35-verifications.md`, le manifeste courant `build-manifest.json` et les sources/tests sont désormais dans ce dossier. SHA-256 courant : `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`. Ne pas réutiliser les chiffres ni l’empreinte du premier essai comme preuve de la nouvelle livraison.

---

# PRE35 SI1 — checkpoint du 25 octobre 2026

## Statut exact

Le calcul a été modifié, compilé et testé dans le vrai client Minecraft. Le JAR final a été produit et vérifié localement. L'environnement d'exécution s'est déconnecté pendant la finalisation de la livraison (environment_offline, erreur 409). AUCUN téléchargement du nouveau JAR n'a encore été enregistré dans les fichiers persistants de l'application. Aucun déploiement ni fusion dans main.

Ce commit est un checkpoint documentaire, pas le commit des sources finales : celles-ci restent dans l'espace de travail déconnecté et doivent être récupérées avant publication. Ne pas présenter cette PR comme une livraison installable.

Issue #178. Conserver les ajouts PRE34/SI1 de #172 et l'optimisation PRE33 de #163.

## Base et binaire figés

- Base : NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar
- Version de base : 1.0.0-beta.34+indivis.1.21.11.SI1
- SHA-256 base : 454bd4206b50737bf386d65eb09f4f0e3ad1294a1fd4e6df426f9fe205cb31d0
- Binaire final : NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar
- Version finale : 1.0.0-beta.35+indivis.1.21.11.SI1
- SHA-256 final : 8138f3ae165a5169b3700fb31be2380a77beb0948e00d0a5bbcefece82e3af10

Espace de travail : /workspace/scratch/22564d9fe6f6/nexus-pre35
Runtime : /workspace/scratch/22564d9fe6f6/pre31
Le binaire final est à la racine du dossier nexus-pre35, et une copie a été préparée dans delivery par tools/package.py. Vérifier l'achèvement du processus de packaging et les fichiers existants après reconnexion.

## Diagnostic des sources fournies

Profil QDXof4JAxf.sparkprofile : client Minecraft 1.21.11, Fabric Loader 0.19.5, Java sampler 4 ms, durée 121,258 secondes, UTC 16:35:49.372–16:37:50.630 le 25/10/2026. Le fuseau Paris doit être converti avec ZoneInfo (17:35–17:37 ce jour-là), ne pas supposer UTC+2 au changement d'heure.

Sur le thread de rendu :
- Nexus : 62,0705 % ;
- PosedSurfaceSupport : 57,7859 % ;
- SurfaceGeometry, branches dédupliquées : 33,3545 % ;
- PreparedSurfaceCache : 0,1002 %.

Les pourcentages sont inclusifs, non additionnables. Le cache de poses ne supprime pas la découpe des poses continuellement nouvelles.

Les logs annoncent PRE34 SI1 côté client et serveur. Iris 1.10.7 indique les shaders désactivés. Des avertissements de retard serveur de 2 à 5,836 s existent également dans le log ; leur cause n'est pas identifiée par ce profil client. Ne pas promettre de les corriger avec deux méthodes de géométrie client.

## Implémentation locale à récupérer

Deux nouveaux helpers de production, et deux corps de méthode remplacés par délégation :
- src/net/tompsen/nexuscharacters/ExactSurfaceClipper.java
- src/net/tompsen/nexuscharacters/ExactSurfaceCleaner.java
- SurfaceGeometry.subtract(Polygon, Box, boolean)
- PosedSurfaceSupport.clean(...)

ExactSurfaceClipper projette les sommets une fois par boîte, conserve les coordonnées projetées des sommets survivants entre les six plans et reprojette chaque nouveau sommet avec la même opération JOML que la base. Il garde les deux formules d'intersection, les seuils EPS, les UV, rangs et l'ordre des plans. Les buffers sont propres au thread. La dernière correction copie la liste de sommets lors d'une émission complète pour ne pas créer un alias mutable avec la liste d'entrée.

ExactSurfaceCleaner réutilise les listes exactes de boîtes pour les mêmes cellules à l'intérieur d'une passe, conserve le tri stable de l'ordre d'origine et évite les listes intermédiaires tant qu'aucun fragment ne change. Cache de requêtes local, plafonné à 2048 ; pas d'approximation de pose ni de liste d'assets codée en dur.

Les 5231 entrées originales ont été auditées : 5228 identiques, dont les 4966 assets et 250 classes. Seuls fabric.mod.json et les deux classes ciblées changent. Quatre classes de support ajoutées, aucune suppression. Les mains, classes serveur, persistance, réseau et SI1 sont identiques à la base.

## Contrôles constatés après le dernier changement du code de production

- EXACT35_PASS sample_cases=24000 grid_modes=481 assertions=3383776
- BYTECODE35_PASS preserved_methods=26 exact_delegates=2 original_QA_bodies=4 no_server_or_protocol_edits=true
- 320 empreintes de géométrie + 91 de cache/invalidation + 96 de modèles partagés identiques : 507 états.
- 23 captures complètes de menus pixel-identiques (ne sélectionner que menu-[0-9][0-9].png, pas les fichiers temporaires *-writing.png).
- PRE32_HAND_PASS baseline=false checks=654 matched_arm_states=55
- PRE30_MENU_PASS captures=23 checks=364
- POSE33_PASS checks=88 geometry_states=91
- HUD33_PASS checks=2518 (HUD existant inchangé).
- PRE30_FUTURE_ASSETS_PASS checks=23 hair=930 beard=930 outfit=930 body=2 classic+slim
- Monde de production PRE35 final : 24 scènes, 144 assertions, 887 images contrôlées, 1970 audits de joueurs distants ; 1,2,4,8 entités vues et 8 également vues en dialogue et F1.
- Monde oracle final : 24 scènes, 144 assertions, 697 images contrôlées.
- ORACLE35_PASS calls=10263 polygons=2973698 vertices=12362474

Le monde oracle est un JAR de test séparé : les corps originaux sont copiés du JAR SI1 puis vérifiés instruction par instruction. L'ancienne et la nouvelle méthode s'exécutent sur les mêmes polygones ET les mêmes instances de boîtes. Comparaison des bits des positions, normales et UV, des rangs, de l'ordre et des tailles. Les méthodes de référence et l'oracle sont absents du JAR livré.

Les autres joueurs sont de vraies entités OtherClientPlayerEntity d'un monde isolé, pas huit connexions réseau. Le renderer distant observé est normal ; slim est vérifié dans les modèles, prévisualisations et mains. Canalisation : contrôleur et animation réels off_hand_channeling de Spell Engine RC17, pas tous les effets ou dégâts. Caméra réelle de Dialogue b10 et retour après arrêt contrôlés.

## Mesures finales isolées, clients successifs

5 échantillons par cas, 24 phases par échantillon, modèles de renderer normal/slim partagés. Chaque phase impose une pose nouvelle ; compteur de recalcul = 24 × nombre de personnages.

| Cas | PRE34 CPU ms/pose | PRE35 CPU ms/pose | CPU en moins | Allocations en moins |
| --- | ---: | ---: | ---: | ---: |
| marche, 1 | 8.256013125 | 6.327603792 | 23.36 % | 41.40 % |
| marche, 4 | 7.722139073 | 6.001078271 | 22.29 % | 41.36 % |
| marche, 8 | 8.125452245 | 6.224461906 | 23.40 % | 41.35 % |
| rotations relatives, 4 | 71.691622427 | 33.535100448 | 53.22 % | 63.95 % |

FINAL35_PERF_PASS sequential_clients=true

Il reste donc un coût : 49,8 ms CPU pour le groupe de huit poses de marche dans ce banc, et environ 33,5 ms par pose dans le cas dense. Ne pas transformer ces gains de calcul en une promesse de 60 FPS dans Lion-Port. L'ensemble des 287 mods, Distant Horizons, les armures personnalisées et le matériel du client ne sont pas reproduits.

## Environnement de vérification

Temurin 21.0.13+9, Minecraft 1.21.11, Loader 0.19.5, Mesa llvmpipe 4.5, 1920x1080.
Fabric API 0.141.6, Skin Layers 1.10.2, TRender 1.0.13 via EntityCulling 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7 sans shaderpack, Accessories 1.4.3-beta patché + NULLCAP-GUARD1, EasyNPC/Config UI 7.16.0, PAL 1.1.10, Spell Engine RC17, Spell Power RC9, RPG RC3, Dialogue b10, HUD OPT1 actuel. Les dépendances servent au test et ne doivent pas être livrées comme remplacements.

## Reprise exacte

1. Reconnecter l'environnement ; ne pas demander de nouvelles captures à Arthur pour reconstruire le diagnostic.
2. Vérifier le SHA du JAR final et les fichiers de nexus-pre35/delivery. Ne pas repartir d'une PRE17/PRE33 ni perdre SI1.
3. Récupérer les sources, scripts et QA locaux dans cette branche.
4. La dernière orchestration a échoué dès apply_patch sur environnement hors ligne : son patch de finition n'a pas été confirmé. Il prévoyait :
   - preuves de monde propres à chaque dossier de jeu et copies distinctes témoin/candidat/oracle, pour supprimer la destination historique commune ;
   - adaptation compare.py/write_report.py vers oracle-world-evidence ;
   - correction du fuseau Paris dans le rapport ;
   - rédaction du maximum réel de retard serveur (5,84 s) sans supposer son fuseau.
   Ces changements concernent le banc et le rapport, pas le calcul de production figé.
5. Si l'on rerun le banc monde, ne pas laisser oracle et production écrire dans la même destination. Les deux marqueurs de fin historiques ont été lus séparément dans les sorties d'outils, mais les fichiers de sortie communs ont ensuite été écrasés.
6. tools/compare.py valide l'audit ZIP, les 507 états, les 23 images, les 20 mesures par version et écrit qa/comparisons.json.
7. tools/package.py génère build-manifest.json, compiled-patches.zip.b64, reconstruit le JAR via assemble.py et compare ses octets ; puis ZIP/JAR/rapport/README dans delivery. Contrôler sa dernière sortie.
8. Enregistrer les livrables avant tout lien final. Fichiers attendus : NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar, Indivis-Nexus-PRE35-SI1.zip, PRE35-verifications.md.
9. PR en brouillon, pas de merge ni d'installation serveur. Le client remplace son seul JAR Nexus ; HUD, autres mods et datapacks inchangés.

Ce checkpoint ne contient pas les profils ou logs bruts d'Arthur.
