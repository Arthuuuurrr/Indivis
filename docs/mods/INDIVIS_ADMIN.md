# Indivis — Administration

Candidat **0.1.0-alpha.1+1.21.11**, non déployé et non testé dans Minecraft/Fabric. Suivi : [#174](https://github.com/Arthuuuurrr/Indivis/issues/174).

Interface native `/indivisadmin` pour sélectionner un joueur connecté et gérer son personnage Nexus actif : rang social, criminalité, cinq réputations, accès, justice, monnaie et diagnostic. OP2 requis ; confirmation liée à la connexion et au personnage. Le core conserve ses règles et seules ses fonctions chargées sont proposées.

Les clients 1.21.11 reçoivent les dialogues natifs sans installation de ce mod. Le JAR est remis séparément ; il ne remplace aucun datapack ou mod existant. Aucun manifeste de production modifié.

[Installation, catalogue, tests et reconstruction](../../sources/indivis-admin/README.md) · [empreinte du JAR](../../sources/indivis-admin/docs/ARTIFACT.json).

Bancs : 19 assertions du contrôleur et 16 de l’adaptateur, API doublées. Vérification ASM : 15 classes/131 méthodes. Restent à vérifier en jeu : ouverture/décodage des dialogues, appels réels au core, Nexus, accès et coexistence des mods.
