# PRE22 — validation fonctionnelle retirée, 2 octobre 2026

## État

Aucune nouvelle version fonctionnelle n'est validée à ce stade. Le JAR PRE22 n'a pas été modifié pendant cette analyse. Ses résultats de tests précédents ne suffisent pas à accepter la demande de rendu.

## Faits et retour utilisateur

- Le log client fourni confirme NexusCharacters 1.0.0-beta.22+hc.1.21.11, Skin Layers 1.10.2, Sodium 0.8.7, Capitale RP HUD 1.3.3 et Player Animation Library 1.1.10.
- Les quatre captures reçues ont été ouvertes et examinées. Trois montrent l'aperçu en monde de création ; une montre une vue proche du corps et des membres.
- Le retour utilisateur indique absence de relief attendu, z-fighting des membres et Steve à la place du skin de compte. Ce retour invalide l'acceptation fonctionnelle annoncée.
- Le log contient des lignes PREPARED pour les géométries ; elles ne prouvent pas que le résultat final affiché est correct.
- Le log client signale un SocketTimeoutException pendant la récupération du profil Mojang. C'est un facteur possible pour Steve ; la responsabilité du chemin de repli ou d'un changement du mod reste à établir.

## Défauts établis du banc de test et du traitement

- SubmittedGeometryAudit.run remet les trois rotations des parties du modèle à zéro avant de reconstruire les surfaces. Les mesures ne reproduisent donc pas fidèlement la pose affichée.
- Son test d'intersection ne conserve que les faces alignées sur les axes et recherche une aire commune dans un plan. Il ne mesure ni les pénétrations de surfaces inclinées ni le scintillement entre images.
- Le contrôle de mesh installé et de zéro intersection ne vérifie pas la fidélité du relief par rapport aux couches de l'asset original.
- GenericLayerPixels.prepareCosmetic remplit chaque pixel supérieur vide correspondant à un pixel inférieur opaque. Pour un asset avec une couche supérieure ajourée déjà dessinée, ce remplissage change l'empreinte du relief. La conservation des fichiers PNG ne garantit donc pas la conservation de la silhouette rendue. L'effet visuel de ce traitement doit être comparé à une extrusion conservant les détails supérieurs d'origine ; il n'est pas à lui seul une explication complète des défauts en jeu.

## Contrôle complémentaire de compatibilité

Player Animation Library Fabric 1.1.10 a été ajouté au runtime réduit. L'ancien test des aperçus passe encore (24 captures, zéro recouvrement dans son périmètre). Ce résultat ne reproduit pas les défauts et ne permet ni d'accuser cette bibliothèque ni de valider PRE22. Son log est conservé dans evidence/rejet-preview-pal.log.

## Suite nécessaire avant acceptation

Reproduire le défaut avec les poses réellement rendues ; comparer le relief aux couches d'origine ; contrôler les articulations dans une séquence d'images ; vérifier la résolution du skin de compte sans confondre préparation, installation du mesh et rendu final. Aucune garantie ni nouvelle version n'est émise sur la base des anciens contrôles.
