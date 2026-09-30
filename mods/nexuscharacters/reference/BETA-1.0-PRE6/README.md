# NexusCharacters BETA 1.0 PRE6

Base: `BETA 1.0 PRE5` (Minecraft 1.21.11).

## Corrections ciblées

- **Barbe segmentée** : géométrie entièrement remplacée. L'ancien empilement de plaques rectangulaires est remplacé par trois mèches segmentées et effilées.
- **Moustaches des barbes 3D custom** : jonction renforcée avec la masse de barbe par chevauchement géométrique. Les styles vanilla/built-in ne sont pas modifiés.
- **Barbe ajourée (`long`)** : silhouette PRE5 conservée, avec raccord moustache/joues renforcé.
- **Menu de création** : correction minimale du repérage du bouton cosmétique. Le layout reconnaît désormais les libellés runtime `Oreilles : …` et `Ornement : …`, afin de réserver leur ligne avant `Créer / Annuler`.
- **Défilement des libellés** : volontairement inchangé.

## Garde-fous

La correction UI ne modifie ni le calcul responsive, ni les dimensions des boutons, ni l'ordre général des lignes. Elle change uniquement deux préfixes de détection dans le mixin existant afin d'éviter une régression du menu.
