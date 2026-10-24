# PQ01 R3-HF10 — charge multijoueur et scènes privées

## Constats dans les trois logs du 24 octobre 2026

Le serveur et le second client utilisent HF9 ; le premier client utilise HF5. Les sessions se chevauchent entre plusieurs joueurs. Ce décalage de version ne prouve pas la cause de la chute de FPS. Le serveur signale un dépassement de 3 053 ms près du démarrage et un délai de marche dépassé pour Gautier ; aucun profil de temps de rendu ou série de MSPT ne permet d’attribuer toute la chute au prologue.

Sur le premier client, Nexus NativeLayers23 rencontre quatre échecs : dépendance `dev/tr7zw/skinlayers/api/OffsetProvider` absente, puis classe NativeVoxelSurface non initialisable et méthode de transition `getTransitionEntity/0` introuvable. Ce client ne charge pas SkinLayers3D ; le second charge SkinLayers3D 1.10.2 et ne montre pas ces mêmes erreurs. Une erreur de synchronisation de conteneur apparaît aussi sur le premier client, sans correspondance suffisante pour l’attribuer à un acteur PQ01. Ces erreurs de rendu restent distinctes ; aucun Nexus modifié n’est fourni dans HF10.

Aucune ligne PERRIN_DIAG n’est présente. La position des points, les obstacles et la vitesse effective de Perrin sur cette carte restent inconnus. Les logs bruts, noms de joueurs et identifiants de compte ne sont pas publiés.

## Modifications

- Le filtre du suivi d’entités utilise un registre typé des propriétaires et des originaux masqués. Une copie d’un autre joueur est refusée avant la réflexion et les allocations des contrôles historiques. L’inscription précède l’insertion dans le monde ; les échecs de création et nettoyages libèrent le registre. Une copie PQ01 orpheline est refusée.
- Le contrôle historique du personnage Nexus et les règles de visibilité des autres quêtes restent exécutés. Aucune règle de rang ou de quête n’est contournée.
- Les demandes PQ01 d’actualisation sont regroupées : un seul parcours natif de visibilité par propriétaire et par tick, au lieu d’un parcours par acteur créé. Le précédent oubli de signature à chaque création est supprimé. Les demandes d’un joueur déconnecté sont éliminées.
- Gautier n’a plus de copie d’ambiance inactive. Sa copie reste créée pour l’interruption de Géraud, son propre dialogue si nécessaire et la scène avec Hugues. La marche commence toujours pendant la réplique de Géraud ; aucun TP de trajet ajouté.
- La validation d’une scène consulte une seule fois le personnage Nexus au lieu de deux.
- Si la ronde de Perrin échoue ou si un point manque, un diagnostic contextualisé est écrit automatiquement et dédupliqué par personnage/phase/cause, sans message dans le dialogue. La commande de diagnostic manuelle reste disponible.

La visibilité personnelle fonctionnait déjà dans le test HF9 de référence : aucune fuite d’acteurs n’y a été reproduite. HF10 renforce le refus explicite et réduit le coût ; il ne présente pas une fuite supposée comme une cause démontrée de FPS.

## Vérifications sur le JAR livré

- **48 contrôles multijoueur** : deux scènes démarrées au même tick, deux marches de Gautier, deux Perrin actifs, observateur et arrivant tardif, fermeture indépendante, changement de personnage et déconnexion. Vrais QuestVisibility, mixin de tracker d’origine, listes de suivi et paquets Minecraft. Aucun paquet de création d’acteur privé étranger observé.
- Le même compteur placé à l’entrée du vrai rafraîchissement constate **2 parcours pour créer deux acteurs en HF9, 1 en HF10**. Les acteurs stables ne sont pas recréés à chaque contrôle de visibilité. Ce n’est pas une mesure de FPS.
- **1161 contrôles de fonctions/capture et audio**, **25 de scène**, **26 en monde Fabric**, **15 d’activité de Perrin**, **44 de lecture client**, **191 de capture b10 intacte**.
- Perrin parcourt un aller/retour de seulement un bloc avant progression, suspend/reprend sa ronde en dialogue et utilise ses points propres au quai. Deux copies marchent aussi pendant les scènes multijoueur, avec une vitesse de source nulle conservée sur l’original.
- PASS ASM BasicVerifier: 182 classes, 1799 methods; no Minecraft API resolution.
- Core strictement identique à HF9 ; datapack de quêtes strictement identique à HF4 ; code client de lecture strictement identique à HF9. Mixins, usine d’acteurs, SequenceBridge, gardes, zones sûres, fonctions de séquence I et hooks de doublage historiques conservés.

## Limites

Les connexions, accusés de réception des chunks et profils Nexus du banc sont synthétiques. Le filtre réseau et la navigation sont réels ; aucune image ni fréquence d’affichage d’un client graphique n’est mesurée. La caméra et le panneau sont doublés dans le test couplé ; la capture native b10 est vérifiée séparément. Aucun parcours complet de la séquence I sur la carte utilisateur. Le gain de FPS avec tous les mods et le Perrin propre à cette carte ne peuvent donc pas être garantis par ces tests.

## Installation

Remplacer le seul JAR de quêtes/journal sur le serveur et chaque client, après arrêt. Conserver core HF9 et `hc_quetes` HF4. Aucun reset, aucune nouvelle liaison ni nouvelle pose. [Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md).

JAR : `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF10.jar`

SHA-256 : `2e8c787bde08555a1ce14e8593c2fead0a95a0dbf72de634880ee8998738e957`
