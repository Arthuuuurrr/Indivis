# NexusCharacters PRE20 — corrections et validation

## Fichier testé

Minecraft 1.21.11, Fabric Loader 0.19.5, Java 21, Skin Layers 3D 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2. Rendu réel du client dans un banc automatisé OpenGL/Mesa ; ce banc ne reproduit pas l'intégralité du modpack du serveur.

SHA-256 du JAR : `274629caa578331de80e69fda7a6830518da22f6ecf80e7742ca8aa5e3d66dd4`.

Base PRE19 : `99a1129b4d38b09d9073a13180cb86332c7d58718a104bfa89a29e034605090e`. Tous les fichiers sous `assets/` sont inchangés, vérifiés lors de l'assemblage. La correction remplace `GenericSkinLayerSupport`, `GenericLayerPixels` et les métadonnées de version ; les autres classes PRE19, dont le catalogue extensible et les barbes déjà modélisées, sont conservées.

## Corrections

- Suppression de l'extrusion supplémentaire de la couche inférieure : elle reste peinte sur le modèle de base.
- Géométrie pixelisée et raccords aux coins produits par `SkinLayersAPI.getMeshHelper().create3DMesh`, avec les transformations natives `OffsetProvider`. Le proxy transmet désormais les opérations de position et de visibilité au maillage.
- Conservation des détails et trous de la seconde couche. Pour une partie cosmétique sans aucun pixel extérieur, génération automatique d'une empreinte extérieure à partir de sa base, sans effacer la base ni remplir les trous d'une seconde couche déjà dessinée.
- Compositing dans l'ordre existant tenue/cheveux/barbe avant création d'un seul maillage extérieur. Les pixels qui se recouvrent ne génèrent plus deux surfaces concurrentes.
- Limites internes des jambes, des manches et des raccords tête/torse/jambes calculées dans les coordonnées du fournisseur natif. Marge de 0,025 pixel par côté sur les raccords vérifiés.
- Retrait des faces dégénérées et des faces internes dupliquées, pour les polygones natifs et la représentation par cubes de compatibilité.
- Conservation des règles natives de distance et d'équipement pour les vrais joueurs, de la visibilité forcée pour les mannequins de l'interface et du raccordement de la manche en première personne.

## Résultats du JAR exact

```
DYNAMIC_CATALOG_PASS hair=15 facial=8 outfits=34
RESOURCE_RELOAD_PASS
CREATION_UI_PASS hair=1006 facial=9 outfit=39
GEOMETRY_AUDIT_PASS combinations=1496 vertices=7246900 faces=1811725 wide+slim=true compatibility=true
PREVIEW_HARNESS_PASS captures=27
WORLD_HARNESS_PASS baseDraws=0 outerDraws=6394
```

L'audit construit les géométries réelles après composition et applique les transformations natives. Il vérifie les sommets finis, l'absence de faces de même géométrie dupliquées, l'absence de face extérieure coplanaire avec les plans du modèle inférieur et les limites des raccords en pose neutre. Les 1 496 cas couvrent 14 coiffures × 34 tenues et 8 barbes classiques × 34 tenues, dans deux représentations géométriques. Chaque maillage est contrôlé pour les bras larges et fins.

Les captures incluent les coiffures 8, 9 et 10 avec la tenue 20 des signalements, d'autres tenues et des barbes classiques. Des ajouts de coiffure, barbe et tenue sont fournis exclusivement au banc par un module de test. Ils sont découverts, accessibles dans l'interface et conservés après rechargement. La coiffure ajoutée à l'identifiant 1006 est une texture HD de 128×128. Les nouveaux assets de test ne sont pas livrés dans le JAR.

Les captures `evidence/hair-clothing-back.png` et `evidence/hair-beard-clothing.png` montrent le dos des cheveux longs et le relief combiné cheveux/barbe/vêtements du client testé. Les neuf aperçus en monde, le joueur réel en troisième personne et la manche en première personne sont également capturés.

Le mode de géométrie de compatibilité a été testé ; cela ne constitue pas un essai avec un shader Iris particulier. Les contrôles de raccords ci-dessus portent sur la pose neutre, pas sur toutes les animations de tous les mods externes. Le comportement attendu à distance reste celui de Skin Layers : retour au rendu plat selon son réglage de LOD.

## Installation

Remplacer l'ancien JAR NexusCharacters par PRE20 dans les installations client et serveur utilisées pour le projet. Conserver un seul JAR NexusCharacters. Skin Layers 3D doit être installé et ses couches activées côté client. Redémarrer le client pour remplacer le code, même si F3+T suffit ensuite pour recharger de nouveaux assets.

## Reproduction de l'assemblage

Depuis le dossier de cette référence :

```
mkdir -p classes
javac -cp /chemin/PRE19.jar -d classes sources/GenericSkinLayerSupport.java sources/GenericLayerPixels.java
python assemble.py /chemin/PRE19.jar /chemin/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE20.jar
```

L'assembleur refuse une base différente de la PRE19 vérifiée et contrôle l'intégrité ZIP ainsi que l'identité de tous les assets. Le workflow GitHub compile la même source et produit un téléchargement d'artefact.
