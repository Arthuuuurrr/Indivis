# NexusCharacters BETA 1.0 PRE7

Base fonctionnelle : `BETA 1.0 PRE6`.

## Corrections ciblées

- **Barbe ajourée** : ajout d'un raccord étroit sous la lèvre inférieure et léger chevauchement des mèches basses. Le bouc ne doit plus apparaître flottant.
- **Nains** : `Aucune` n'est plus un état valide de pilosité dans le créateur.
  - en arrivant sur un Nain avec une barbe absente/invalide, le sélecteur force `Barbe courte` ;
  - après la dernière barbe naine, le cycle revient directement à la première barbe au lieu de passer par `Aucune`.
- **Menu / responsive / défilement des libellés** : inchangés par rapport à PRE6.

## Portée volontairement limitée

PRE7 ne retouche pas le layout ni les autres modèles de barbe. La correction du Nain est faite dans le sélecteur unifié existant afin d'éviter une nouvelle régression de l'écran de création.
