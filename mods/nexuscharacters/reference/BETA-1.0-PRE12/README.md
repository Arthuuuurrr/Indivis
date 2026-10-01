# NexusCharacters — BETA 1.0 PRE12

Base exacte : **BETA 1.0 PRE11**.

## Cause des deux problèmes restants

### 3D Skin Layers absent

3D Skin Layers ne lit pas le `SkinTextures` remplacé par Nexus dans le render state. Son code demande l'identifiant de skin à `TRansition PlayerUtil.getPlayerSkin(player)`, qui renvoyait encore le skin de compte/GameProfile.

Le mod 3D Skin Layers fonctionnait donc normalement, mais sur **le mauvais skin**. Il ne recevait jamais la texture dynamique `nexuscharacters:dynamic/appearance/...`.

PRE12 ajoute un hook optionnel et ciblé sur `PlayerUtil` : uniquement lorsqu'un joueur possède un tag `nexuscharacters.skin.*`, l'identifiant retourné devient celui de la texture dynamique déjà générée par Nexus. 3D Skin Layers peut alors exécuter son pipeline normal : tête, veste, manches et pantalons en voxels 3D.

### Cheveux longs fusionnés avec le corps

Le correctif PRE2 avait copié les pixels de cheveux longs du torse de la couche de base vers la surcouche `jacket`, mais sans supprimer les pixels d'origine.

Chaque coiffure longue concernée existait donc simultanément :
- collée au corps ;
- et sur la surcouche.

PRE12 supprime uniquement cette copie **sur la couche de base** pour les cheveux longs 01 à 05. La copie de surcouche est conservée, donc :
- sans Skin Layers : la coiffure reste visible sur la couche vanilla externe ;
- avec Skin Layers : cette couche est voxelisée par le mod.

## Sécurité

- Le hook TRansition est `@Pseudo` et `require=0` : Nexus continue de fonctionner si 3D Skin Layers n'est pas installé.
- Seuls les joueurs portant un tag Nexus sont concernés.
- Le renderer expérimental PRE10/PRE11 est désactivé ; 3D Skin Layers reprend lui-même le rendu.
- Aucun changement de sauvegarde, réseau, barbes, races, UI ou compétences.
