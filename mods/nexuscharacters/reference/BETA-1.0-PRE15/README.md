# NexusCharacters — BETA 1.0 PRE15

Base exacte : **BETA 1.0 PRE14**.

## Cause réelle du test négatif PRE14

Le log client du 01/10/2026 montre que PRE14 n'a jamais atteint la création du mesh 3D :

`java.lang.NoClassDefFoundError: net/tompsen/nexuscharacters/LongHair3DRenderSupport$HairParts`

La source PRE14 déclarait `HairParts` comme record interne. `javac` produisait donc deux fichiers :

- `LongHair3DRenderSupport.class`
- `LongHair3DRenderSupport$HairParts.class`

Le workflow PRE14 n'embarquait que le premier dans le JAR. Dès le premier appel à `getParts()`, le client échouait avant même d'appeler `MeshHelper.create3DMesh`. Le fallback plat restait alors actif, ce qui explique à la fois :

- l'absence totale de cheveux 3D ;
- le z-fighting encore visible sur le corps ;
- l'absence de la ligne de succès PRE14 dans le log.

## Correction PRE15

PRE15 supprime complètement cette dépendance à une classe interne : le cache utilise désormais un simple `Object[]` `{headPart, bodyPart}`. Le compilateur ne doit produire **qu'un seul** `LongHair3DRenderSupport.class`.

Le build vérifie explicitement :

- qu'aucun `LongHair3DRenderSupport$*.class` n'est généré ;
- que le JAR ne référence plus `LongHair3DRenderSupport$HairParts` ;
- que l'initialisation hair-only peut aller jusqu'à la construction des HEAD/BODY parts dans un harness de compatibilité ;
- que les signatures de 3D Skin Layers 1.10.2 restent celles attendues.

## Écartement

Le rendu 3D n'ayant jamais été actif en PRE14, l'écartement PRE14 n'a en pratique jamais été testé.

PRE15 augmente néanmoins volontairement la séparation BODY :

- X : `1.16`
- Y : `1.02`
- Z : `1.45`

Après l'offset BODY natif de Skin Layers (`1.05 / 1.035 / 1.15`), cela donne environ :

- X : `1.218`
- Y : `1.056`
- Z : `1.668`

soit environ un pixel de marge supplémentaire en profondeur par rapport à la jacket 3D, ce qui vise directement le z-fighting observé.

Les assets, les palettes de couleur PRE14 et les autres systèmes Nexus restent inchangés.
