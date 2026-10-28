# Nexus PRE35 SI1 OPT2 — vérifications du 28 octobre 2026

Version : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT2`. Correctif cumulatif : OPT1 est inclus.

## Installation

Fermer complètement Minecraft. Remplacer l’ancien JAR Nexus dans le dossier `mods` de l’instance réellement utilisée. Ne conserver qu’un JAR Nexus. Le chargement côté client est indispensable pour profiter du correctif de rendu.
Conserver la liaison Indivis-Nexus-NPC PREVIEW1, EasyNPC et Skin Layers. Aucun nouveau fichier de configuration, preset ou asset à installer. Aucun déploiement automatique sur le serveur.

Ligne attendue dans le journal du client après redémarrage :

```text
nexuscharacters 1.0.0-beta.35+indivis.1.21.11.SI1.OPT2
```

## Ce qui change

- Listes de sommets et de polygones dimensionnées au besoin pendant les transformations, pour éviter leur surallocation.
- Mêmes appels Vertex.transform, opérations JOML, coordonnées, normales et UV. Aucun recyclage des objets retournés.
- Aucun LOD, suppression de relief, saut d’animation ou changement d’EPS/espacement.
- Les nouvelles tables de requêtes, listes de fragments recyclées et recherches de classes mémorisées ont été retirées après les mesures. Elles réduisaient certaines allocations mais ne donnaient pas un gain CPU fiable, avec des ralentissements dans plusieurs cas. Le clipping SpatialSurfaceCleaner, GenericSkinLayerSupport et LowerJointSupport restent identiques à OPT1.

## Contrôles terminés sur ce JAR

- Compilation reproductible Java Temurin 21.0.13+9 ; 4966 entrées d’assets inchangées octet pour octet.
- 2400 cas de transformation (28800 polygones), dont entrées non finies/singulières et propriété des sorties.
- 10368 tests de faces extérieures et 5400 configurations/cas de découpe contre le code PRE35 conservé.
- 13 méthodes originales de SurfaceGeometry préservées instruction par instruction ; la transformation originale est conservée comme oracle.
- Oracle en véritable client : 2537500 polygones de découpe et 4401720 polygones transformés comparés valeur flottante par valeur flottante ; aucune différence.
- 300 réutilisations de parties exercées avec vérification de l’oracle.
- 216 états PNJ/joueurs identiques entre les trois processus PRE35 SI1, OPT1 et OPT2 ; poses réellement nouvelles pendant les mesures.
- 23/23 captures de menus identiques pixel par pixel à OPT1, création/sélection, races et rotation.
- 466 états supplémentaires de géométrie, caches et bras identiques à OPT1, corps normal/slim.
- Export par le véritable éditeur, imports natifs EasyNPC, rendu des couches cosmétiques et absence de fuite d’état sur un PNJ ordinaire.
- Nouveaux IDs 930 de coiffure, barbe classique et tenue découverts et rendus sur un vrai PNJ, sans changement du code de production.
- Réglage Skin Layers modifié à chaud 1.05 → 1.15 → 1.05 sur un vrai modèle : variantes distinctes puis restauration.
- Monde réellement fermé puis rouvert : dix PNJ, UUID, recettes et cinq tags témoins conservés. Vues de face/dos inspectées.

## Mesures CPU et allocations

Préparation native groupée des modèles sur le thread de rendu : extraction d’état et calcul des surfaces. Ce ne sont pas des FPS et le dessin GPU, le monde et la soumission des accessoires ne sont pas inclus.
Même environnement, mêmes recettes, processus séparés : PRE35 SI1, OPT1 puis OPT2. Échauffement global de 320 images à trois entités, puis 1500 images statiques ou 64 images animées par cas. Médiane de cinq séries de 24 passages. Le nombre de recalculs est contrôlé pour exclure un faux gain par répétition de poses déjà en cache.
Oracle désactivé pendant les chronométrages. Toutes les lignes, y compris les variations défavorables, sont fournies dans MESURES_CPU.csv.

| Type | Pose | Entités | SI1 CPU ms | OPT1 CPU ms | OPT2 CPU ms | SI1 allocations Mio | OPT2 allocations Mio |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: |
| PNJ | Immobile | 1 | 0.053 | 0.056 | 0.054 | 0.009 | 0.009 |
| PNJ | Immobile | 3 | 0.154 | 0.119 | 0.117 | 0.028 | 0.028 |
| PNJ | Immobile | 8 | 0.198 | 0.169 | 0.117 | 0.073 | 0.073 |
| PNJ | Tête mobile | 1 | 8.194 | 3.437 | 3.462 | 9.080 | 3.292 |
| PNJ | Tête mobile | 3 | 34.001 | 14.671 | 14.044 | 35.228 | 13.140 |
| PNJ | Tête mobile | 8 | 91.822 | 39.646 | 37.342 | 94.759 | 35.789 |
| PNJ | Marche | 1 | 7.422 | 4.274 | 4.354 | 8.373 | 3.985 |
| PNJ | Marche | 3 | 30.624 | 17.130 | 17.183 | 34.272 | 15.997 |
| PNJ | Marche | 8 | 82.310 | 48.115 | 47.862 | 92.183 | 43.310 |
| Joueur | Immobile | 1 | 0.118 | 0.122 | 0.266 | 0.038 | 0.038 |
| Joueur | Immobile | 3 | 0.378 | 0.702 | 0.861 | 0.111 | 0.111 |
| Joueur | Immobile | 8 | 0.753 | 0.785 | 0.828 | 0.292 | 0.292 |
| Joueur | Tête mobile | 1 | 8.355 | 3.521 | 3.450 | 9.093 | 3.304 |
| Joueur | Tête mobile | 3 | 34.412 | 14.147 | 14.322 | 35.388 | 13.239 |
| Joueur | Tête mobile | 8 | 94.159 | 43.773 | 43.870 | 95.122 | 39.947 |
| Joueur | Marche | 1 | 9.187 | 4.354 | 4.361 | 8.388 | 4.010 |
| Joueur | Marche | 3 | 31.945 | 17.579 | 17.684 | 34.464 | 16.106 |
| Joueur | Marche | 8 | 84.123 | 48.247 | 47.831 | 92.503 | 43.539 |

## Portée et limites

Client Linux en rendu logiciel llvmpipe, Minecraft 1.21.11, Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11, EasyNPC 7.16.0 et liaison PREVIEW1 inchangée. Dépendances principales : Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7, PAL 1.1.10, owo 0.13.0, Cloth Config 21.11.153, Pufferfish Skills 0.19.0 et HUD OPT1.
Le pack de production complet, les véritables sorts/caméra de dialogue et les comptes Mojang authentifiés ne sont pas reproduits dans cette session OPT2. Aucun chiffre FPS de Lion-Port n’est revendiqué.
Le compte synthétique hors ligne conserve les avertissements PRE35 sur le manifeste propriétaire du vault. Les classes de persistance ne sont pas modifiées ; leur validation complète n’est pas prétendue. Le rechargement des véritables PNJ EasyNPC est vérifié séparément.
Les nouveaux assets restent soumis aux formats UV et au catalogue Nexus ; le test 930 n’est pas une garantie pour un PNG arbitraire mal formé.
Aucune fusion dans main et aucun déploiement sur le serveur de production.

## Identité

SHA-256 OPT2 : `7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116`
SHA-256 OPT1 : `bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11`
SHA-256 PRE35 SI1 : `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`
