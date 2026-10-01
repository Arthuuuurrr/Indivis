# NexusCharacters — BETA 1.0 PRE10

Base exacte : **BETA 1.0 PRE9** `1.0.0-beta.9+hc.1.21.11`.

## Objet

PRE10 traite deux défauts visuels liés aux coiffures longues :

1. la partie qui descend sur le torse se confond avec les vêtements, surtout quand la tenue utilise elle-même la surcouche `jacket` ;
2. les pixels de cheveux sur la surcouche peuvent donner une impression de pixels détachés / flottants.

## Diagnostic

Depuis PRE2, les pixels de torse des coiffures longues 01..05 sont copiés sur les UV de surcouche du torse afin d'éviter le z-fighting. Cela place cheveux et vêtements sur **la même couche géométrique** lorsque la tenue possède elle aussi une surcouche. Le compositeur applique ensuite la coiffure après la tenue, ce qui accentue visuellement leur fusion.

## Correction PRE10

Quand **3D Skin Layers** est disponible :

- la texture dynamique du joueur reçoit une variante **head-only** des coiffures longues 01..05 ;
- les pixels de cheveux du torse ne sont donc plus fusionnés dans la même `jacket layer` que la tenue ;
- ces pixels sont rendus séparément avec l'API publique `MeshHelper` de 3D Skin Layers ;
- le positionnement utilise le même `OffsetProvider.BODY` que le mod ;
- une séparation supplémentaire très légère est appliquée au mesh de cheveux (surtout en profondeur) pour éviter que les deux volumes se confondent ;
- la couleur reste liée au choix de couleur de cheveux Nexus.

Les cheveux longs 06..08 n'ont actuellement pas de pixels sur le torse et restent inchangés.

## Garde-fous

- Intégration **optionnelle par réflexion** : aucune dépendance dure à 3D Skin Layers.
- Si son API n'est pas disponible, Nexus réutilise les textures longues PRE9 originales et conserve donc le rendu précédent au lieu de perdre la partie basse des cheveux.
- Les textures originales PRE9 ne sont pas modifiées.
- Aucun changement de sauvegarde, réseau, identité, barre de compétences, pilosité, barbe ou layout.
- Le correctif PRE9 `allowsPilosity(Object)` reste byte-identique.
- Le correctif PRE8 de silence console reste byte-identique.

La mise en œuvre utilise uniquement l'API publique exposée par 3D Skin Layers ; aucun code du mod tiers n'est embarqué dans NexusCharacters.
