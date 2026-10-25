# PQ01 R3-HF11 — marche de Gautier

25 octobre 2026.

## Faits observés

Le serveur et le client chargent le journal HF10. Le log serveur indique un début de déplacement vers 14:00:16 puis une expiration à 14:00:31, soit les 300 ticks du contrôleur. Il ne contient pas la vitesse ni les coordonnées du Gautier à cet instant ; sa valeur réelle ne peut donc pas être déduite. Les très grands messages de retard observés à la reprise du serveur précèdent cette scène et ne démontrent pas un ralentissement durable pendant l’approche. Arthur indique ne pas avoir eu de chute de framerate lors de ce passage.

Le fichier Spark joint est bien un profil client avec Render thread. Il couvre 14:58:49.929–14:59:50.364, heure de Paris, avant la conversation de Géraud qui commence vers 15:00:12 côté client. Il ne mesure donc pas ce trajet. Un second lien est mentionné après une capture de deux minutes, mais ses données n’ont pas pu être récupérées. Le correctif est fondé sur le défaut de marche reproduit ci-dessous, sans nouvelle attribution au rendu. Aucun log brut ou identifiant de compte n’est publié.

## Défaut reproduit en monde Minecraft

La correction HF9/HF10 ne relevait la vitesse de base d’une copie mobile que si elle était nulle ou négative. Une vitesse faible mais positive restait donc héritée du PNJ de décor. Avec une source à 0,02, le vrai HF10 laisse la copie à 0,02 : le test de marche EasyNPC n’atteint pas le point à neuf blocs et expire exactement à 300 ticks.

Ce test démontre une faiblesse du code et un cas compatible avec le retour ; il ne prétend pas mesurer la vitesse du PNJ de la carte utilisateur. Le JAR HF10 reconstruit pour cette comparaison possède exactement le SHA-256 de la livraison : `2e8c787bde08555a1ce14e8593c2fead0a95a0dbf72de634880ee8998738e957`.

## Changement

- Plancher de vitesse de base 0,25 pour les seules copies mobiles de Gautier et Perrin. Les valeurs de base supérieures restent conservées ; les originaux ne sont pas écrits.
- Coefficient de navigation de Gautier 1,0, au lieu de 0,65. Perrin conserve son coefficient 0,65 et ses pauses.
- Un trajet qui expire écrit sa position, sa destination et sa distance restante dans le journal serveur, sans message dans la conversation.

Le délai reste borné à 300 ticks par défaut. Aucun TP de scène, nouvelle boucle de recalcul, modification de géométrie ou reset ajouté.

## Vérifications sur le JAR livré

- **12 contrôles supplémentaires de marche réelle**. Source à 0,02 : approche en **83 ticks**, retour en **75 ticks** ; trajet de 24 blocs en **185 ticks**, retour en **157 ticks**. La source garde sa vitesse 0,02 et sa position. Aucun saut instantané de position.
- **48 contrôles multijoueur**, avec Gautier source à 0,02 et Perrin source à zéro : deux scènes simultanées, observateurs, fermeture indépendante, changement de personnage et déconnexion. Aucun paquet de création d’acteur privé étranger.
- **1161 contrôles de fonctions/capture/audio**, **25 de scène**, **26 de monde Fabric**, **15 d’activité de Perrin**, **44 client**, **191 de capture b10 intacte**.
- PASS ASM BasicVerifier: 182 classes, 1799 methods; no Minecraft API resolution.

Les textes, pages manuelles, choix verts, 92 prises et hooks de doublage sont conservés. Core identique à HF9 ; `hc_quetes` identique à HF4. Code de lecture client, filtre QuestVisibility, Pq01Actors, mixins et intégrations historiques identiques à HF10.

## Installation et limites

Remplacer seulement le JAR de quêtes/journal sur le serveur et chaque client après arrêt. Conserver core HF9 et hc_quetes HF4. Aucun PNJ, point, liaison ou progression à réinitialiser. [Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md).

Le test emploie le vrai monde Fabric, le contrôleur de production et la navigation EasyNPC sur marches/dalles et sur 24 blocs. Les connexions et profils Nexus sont synthétiques ; la caméra et le panneau sont doublés dans la scène couplée, la capture b10 est testée séparément. La géométrie du navire et la vitesse réelle de ses PNJ ne sont pas disponibles. Aucune validation de FPS ou parcours complet de la séquence I revendiqué.

JAR : `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF11.jar`

SHA-256 : `1f63a074ae4871d02daa0c70729032b3985f9b5029548a9539ae5627625a1f83`
