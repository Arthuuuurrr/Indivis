# Architecture et compatibilité R3

## Base réellement utilisée

Datapacks SI1/SI1b et journal b7 SI1b fournis, signatures SHA-256 dans les audits. Le moteur de dialogues R2 est reconstruit sur ces mêmes entrées, puis complété par le pont natif R3. Aucun ancien Nexus n’est substitué ; la classe de politique testée est extraite de `PRE34-SI1` fourni, sans modification.

Seuls la métadonnée Fabric et le début de `QuestVisibility.isVisibleTo` sont modifiés parmi les entrées originales du journal. Les 159 autres classes originales sont identiques octet pour octet, notamment `SequenceBridge`, les liaisons des gardes et le moteur du journal. Les nouveaux fichiers sont le pont PQ01, le contrôleur de marche, le registre d’acteurs, l’initialiseur, les identifiants générés et deux mixins ciblés. Les contrats de compilation et les outils ASM ne sont jamais intégrés au JAR livré.

## Scène individuelle

Les copies EasyNPC partent du NBT du PNJ existant. Identité, actions de proximité/travail, objectifs, commerce, passagers, laisse et anciens tags sont exclus ; les propriétés visuelles et équipements sont conservés. L’action d’interaction de la copie est uniquement celle de son rôle. Les invocations sont bornées aux UUID de liaisons, à la même dimension et à un rayon de 24 blocs autour du source. Aucun scan d’entités du monde n’est ajouté par tick ; seules les vues actives sont suivies, avec recherche par UUID des quatre matelots proches une fois par seconde et par joueur.

La visibilité par personnage réutilise SI1b. Elle est complétée par les protections de collision/poussée, l’équipe sans collision, l’absence de goals/target goals réintroduits, l’interdiction de ramassage/drops/monture/laisse, et les hooks sans effets sur plaques/pas/vibrations. La nav au sol conserve les collisions avec le décor ; les acteurs ne traversent pas les murs. Aucun PNJ original ni son IA ne reçoit ces modifications de scène.

Gautier et Perrin utilisent le navigateur au sol EasyNPC, l’IA de déplacement active et les objectifs de comportement retirés. Replanification tous les quarante ticks ; timeout, acteur disparu, changement de personnage/dimension et fermeture déclenchent une annulation ou un repli. Les acteurs sont supprimés à la fermeture/éloignement/déconnexion, et les orphelins d’un arrêt anormal au chargement de l’entité.

Le retour après fermeture reprend la lecture stockée sans répéter une réplique de Gautier déjà affichée. La caméra suit uniquement la session capturée par PQ01 ; fermer une ancienne vue ne ferme pas un dialogue plus récent d’une autre quête. L’ouverture de caméra est différée d’un tick après création de l’acteur.

## API contrôlées

Les signatures EasyNPC 7.16.0 et Dialogue b10 sont lues par `javap` dans les JAR fournis : `getGroundPathNavigation`, `getEntityGoalSelector`, `getEntityTargetSelector`, attributs immuables de mouvement/interaction/inventaire, `onFermeture(BiConsumer)`, `ouvrir`, `session`, `fermer`, `pnjOccupe`, `sondeMouvement`. Les helpers package-static `SequenceBridge` et `GuardNbt` sont réutilisés tels quels. Le dossier `audit/api` conserve ces lectures.

Mappings Minecraft vérifiés dans les références primaires Yarn 1.21.11+build.4 :

- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/Entity.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/mob/MobEntity.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/ai/goal/GoalSelector.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/ai/pathing/EntityNavigation.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/world/World.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/server/command/CommandManager.html

Ces contrôles de signatures et de mapping ne remplacent pas un démarrage Fabric avec tous les mixins ni une recette du monde. Le bridge utilise la même méthode de réflexion et le même espace intermediary que le journal fourni ; aucun remapping des classes historiques n’est effectué.
