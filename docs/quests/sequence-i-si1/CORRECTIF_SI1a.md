# Correctif SI1a — capture du garde et lecture NBT

Les traces fournies montrent que les fonctions SI1 sont chargées et appelées. La capture du modèle a été refusée quatre fois et la pose d'une Sentinelle a retourné 0. Ce n'est pas une commande manquante dans le guide.

Deux défauts de l'adaptateur livré ont été corrigés :

- `NbtCompound.method_10551(String)` supprime une entrée ; la lecture correcte est `method_10580(String)`. La mauvaise méthode touchait la lecture des coordonnées et des données de modèle. Les suppressions conservées sont explicites et concernent uniquement la copie destinée au clone.
- La capture exécutait des commandes de copie puis lisait immédiatement leur résultat, ce qui dépendait de l'ordonnancement du moteur de fonctions. La capture et la préparation enregistrent désormais les données par les API natives, de façon synchrone.

Le garde admissible le plus proche est recherché parmi les EasyNPC statiques du Cœur dans un rayon de huit blocs. Un PNJ décoratif plus proche ne masque plus le bon garde. Les patrouilles sont exclues. Le modèle complet est copié, conservé et réutilisable pour les deux Sentinelles. Le garde source reste intact. La pose affiche une explication lorsque le modèle manque ; `hc_quetes:pose/inspecter_garde` décrit les PNJ proches.

## Mise à jour d'une installation SI1 existante

1. Arrêter le serveur et remplacer seulement le journal par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1a.jar`, et le datapack de quêtes par `hc_quetes_b22_V2_SEQUENCE_I_SI1a.zip`. Retirer leurs deux anciennes variantes actives. Remplacer aussi le journal côté clients pour conserver les mêmes versions.
2. Le core `capitale_core_1.5.7_V2_SEQUENCE_I_SI1.zip` et Nexus `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar` sont identiques, octet pour octet, à SI1. Les laisser en place s'ils sont déjà installés.
3. Redémarrer. Près d'un garde du Cœur statique fonctionnel, exécuter `/function hc_quetes:pose/reference_garde`. Le message doit confirmer que le modèle est prêt.
4. Aux deux postes voulus, exécuter respectivement `/function hc_quetes:pose/pnj/sentinelle_archives_1` puis `/function hc_quetes:pose/pnj/sentinelle_archives_2`.
5. Si les coins des Archives ou le port avaient déjà été configurés, réenregistrer leurs positions avec les trois fonctions indiquées dans le guide. La mauvaise lecture NBT a pu retirer des coordonnées ; cette livraison ne peut pas les reconstituer sans votre carte. Aucun effacement des quêtes ou des profils n'est nécessaire.

En cas de refus de capture, `/function hc_quetes:pose/inspecter_garde` affiche pour chaque EasyNPC dans les huit blocs sa distance et la reconnaissance de ses contrôles statiques. Ce diagnostic ne modifie pas le garde.

Le correctif ne change ni les états de progression, ni les préfixes Nexus, ni le schéma de migration. Il ne supprime pas de PNJ existant lors de l'installation. Une pose répétée reste soumise au verrou du poste déjà enregistré ; le guide décrit sa libération volontaire.

## Preuves et limite

Le test de lecture non destructive échoue sur le JAR SI1 précédemment livré (`audit/nbt_red_before_fix.log`) et passe avec SI1a. Le banc NBT vérifie la capture au premier appel avec commandes différées, un PNJ décoratif plus proche, deux préparations successives, la conservation du modèle et des cosmétiques, le raccordement aux Archives et l'absence de modification du garde source. Il utilise des doubles des API Minecraft et EasyNPC ; il ne prouve pas le chargement du correctif ni le clonage dans le jeu.

Les contrats de méthodes ont été vérifiés dans les Javadocs Yarn officielles de Minecraft 1.21.11 build 1 :
- https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/nbt/NbtCompound.html
- https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/command/EntityDataObject.html
- https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/command/DataCommandStorage.html

La recette en jeu reste à effectuer : capture, deux poses, apparence/équipement, ancrages et contrôle d'accès. Les anciens contrôles statiques et bancs sont également réexécutés. Les logs privés fournis ne sont pas inclus dans la livraison ni publiés sur GitHub.
