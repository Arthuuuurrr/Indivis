# Indivis — PQ02 R1 HF8 : installation

Cible : Minecraft/Fabric **1.21.11**, Java **21**. HF8 reprend les corrections de dialogue, d’escorte et de sauvegarde de HF7 et applique la décision du 1er novembre : **50 Martins versés pendant F06, quelle que soit la réponse**, sans retour pour récupérer la bourse.

## Mise à jour depuis HF7 ou une version PQ02 précédente

**Aucune commande, liaison, pose de PNJ ou remise à zéro à refaire.** Installer les trois fichiers suivants, après arrêt du serveur et sauvegarde cohérente du monde, des profils Nexus et des configurations.

| Destination | Fichier | Rôle de la mise à jour |
| --- | --- | --- |
| Serveur et chaque client : `mods/` | `haute-capitale-quetes-HF12-PQ02-R1-HF8-1.21.11.jar` | Remplace le précédent mod de quêtes intégré ; versement et dialogues F06 corrigés. |
| Serveur : `<monde>/datapacks/` | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8.zip` | Remplace le core HF1 : versement limité à la scène et achèvement conditionné au don reçu. |
| Serveur : `<monde>/datapacks/` | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8.zip` | Remplace `hc_quetes` R1 : description de la récompense actualisée dans le journal. |

Décompresser `PQ02_HF8.zip` ou l’archive complète de livraison. Les deux ZIP de datapack contenus dedans ont `pack.mcmeta` à leur racine et peuvent être installés directement. Remplacer aussi toute ancienne copie décompressée du même pack. Le dossier `mods/` du correctif contient **un seul mod de quêtes**, identifiant `haute_capitale_quetes`, à copier sur serveur et clients.

Conserver les autres mods déjà installés : NexusCharacters OPT3, Dialogue b10, EasyNPC et ConfigUI 7.16, RPG, monnaie et compétences. Retirer l’ancien compagnon `indivis-pq02` s’il subsiste. Les dépendances ne sont pas redistribuées. La monnaie livrée reste `capitale_currency:martin_dor` ; aucune substitution de test ne figure dans la livraison.

Redémarrer complètement le serveur et les clients. Un simple `/reload` ne met pas à jour les classes et mixins. Reprendre le clic normal sur Léovic, avec le personnage concerné.

## Première préparation seulement

Les commandes ci-dessous restent celles de HF1/HF7. Si elles ont déjà été faites correctement, passer directement au jeu.

## Préparation des PNJ existants

Les UUID et les coordonnées sont dans le monde, pas dans le ZIP du core. Aucun nouvel Althéon, Léovic public ou magistrat permanent n’est créé par l’installation.

Près du **Léovic permanent des tentes**, à moins de quatre blocs, avec les autres PNJ plus éloignés :

```mcfunction
/function capitale:pq02/liaison/leovic
```

Près du **magistrat existant sous les Archives**, même distance :

```mcfunction
/function capitale:pq02/liaison/magistrat
```

Ces commandes enregistrent son UUID, son type et son apparence pour les copies personnelles. Elles ne remplacent ni ses actions publiques ni son skin. Une sélection ambiguë est refusée. Effectuer ces liaisons avant la première scène, sans autre escorte PQ02 en cours. Une reconnaissance automatique des anciens tags/actions est également prévue ; la liaison explicite rend la préparation vérifiable.

Depuis **n’importe quel point du parcours dans la même dimension**, préparer les repères existants :

```mcfunction
/function capitale:pq02/admin/preparer_parcours
/function capitale:pq02/admin/diagnostic_points
/function capitale:pq02/admin/diagnostic
```

La préparation recherche les véritables entités **`minecraft:marker` chargées dans la dimension**, sans dépendre de la position, de l’altitude ou du repère regardé par l’administrateur. Elle mémorise leurs positions réelles, conserve les positions déjà mémorisées pour les repères non chargés et refuse les doublons. Elle ne charge aucun chunk. Si des points requis sont absents, parcourir les portions concernées et relancer `preparer_parcours` : les positions déjà obtenues restent mémorisées. Aucune coordonnée de remplacement n’est inventée et aucun waypoint n’est déplacé.

Le diagnostic détaillé indique, pour chaque point, son rôle, ses coordonnées et son état **chargé / mémorisé / absent / ambigu**. Il est en lecture seule. Le diagnostic général conserve aussi les informations de personnage, scène et progression.

L’ancien guide est un **armor stand invisible portant `guide_leovic_registre_port`**. Les petites armures visibles A, D, H, etc. ne sont que des visualisations et ne sont jamais sélectionnées comme guides ou markers par la préparation. Si le parcours est complet, qu’un seul ancien guide est chargé et que A est chargé, la commande retire ses états de marche et le stationne sur **A**, même si elle est exécutée près de D, M ou P. Elle diffère ce stationnement si A est seulement mémorisé ou si une scène PQ02 est active. Elle ne téléporte pas directement le PNJ public ; son éventuel suivi de l’ancien guide dépend de sa configuration EasyNPC existante.

**La préparation ne démarre pas la quête.** Léovic reste à son poste hors escorte ; sa copie personnelle démarre après l’acceptation dans le dialogue. L’ancienne commande `/function capitale:pq02/admin/preparer_guide_historique` reste disponible comme alias de `preparer_parcours`.

Pour afficher ou masquer les armures de visualisation sans modifier les vrais repères :

```mcfunction
/function capitale:pq02/admin/afficher_points
/function capitale:pq02/admin/masquer_points
```

| Points existants | Rôle dans le trajet |
| --- | --- |
| A | Départ près du poste de Léovic ; stationnement de l’ancien guide |
| B, C | Progression sur le quai |
| D | Première pause de lore |
| E, F, G | Suite du quai |
| H−, H, H+ | Approche, seconde pause et sortie ; H−/H+ facultatifs |
| I, J, K, L | Approche du magistrat |
| M | Arrivée au magistrat |
| N, O, P | Trajet jusqu’à l’ascenseur ; arrivée à P |

Le trajet requis est **A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P** ; les deux repères historiques `h_before` et `h_after` sont utilisés lorsqu’ils existent. Le magistrat reste à M, l’ascenseur à P. Les fonctions de placement historiques de ces points restent disponibles et inchangées. Le mod privilégie toujours un marqueur chargé à son ancienne valeur en cache.

Pour une Sentinelle du Cœur réellement présente qui n’a pas déjà le tag d’Archives reconnu, se placer près d’elle et utiliser, au besoin :

```mcfunction
/function capitale:pq02/liaison/sentinelle
```

Cette commande ajoute seulement une étiquette d’identification à la Sentinelle sélectionnée. Elle n’attribue aucune connaissance au joueur. La rubrique ne se débloque qu’en escorte, devant une Sentinelle visible, dans le champ avant du joueur, à portée et sans obstacle de vue. La simple mention des Sentinelles dans T04 ne suffit pas.

## Jouer et reprendre

Le joueur commence par l’interaction normale avec Léovic. L’escorte utilise une copie personnelle ; les autres joueurs continuent de voir le PNJ permanent. Les conversations facultatives s’ouvrent au clic sur **son** Léovic. Le déplacement et son délai de navigation restent suspendus pendant le menu.

Les répliques successives d’un même bloc sont regroupées. **Espace, Entrée ou clic hors d’un choix** permet de passer au bloc suivant après la dernière page et la fin de la frappe. Les choix de réponse fonctionnent normalement. Aucun minuteur ne fait disparaître une réplique avant lecture. Les voix configurées, le cas échéant, doivent finir avant l’avancement.

Fermer volontairement une conversation de trajet reprend l’escorte ; « Reprenons notre route » la reprend avec la réplique prévue. Une déconnexion sauvegarde le dernier point et la position de scène, puis retire les acteurs. Après reconnexion, interagir avec le guide restauré pour reprendre. Si le joueur s’éloigne de plus de 18 blocs, Léovic attend ; il ne se téléporte pas pour le rattraper. Un obstacle provoque un arrêt borné et un diagnostic serveur, jamais une téléportation à travers le décor.

Pour libérer **uniquement sa propre** scène après une intervention d’administration, sans récompenser ni réinitialiser la quête :

```mcfunction
/function capitale:pq02/admin/nettoyer_scenes
```

La progression conservée permet une reprise ; cette commande n’est pas un outil de remise à zéro de personnage. Ne pas utiliser les anciennes fonctions d’achèvement ou de don pour sauter le scénario : elles ouvrent désormais le dialogue approprié.

## Don, réponses et sauvegardes

À l’entrée dans F06, après l’inscription et l’escorte jusqu’à l’ascenseur, le personnage reçoit **50 Martins une seule fois**, avant de choisir sa réponse. Aucun sous-menu d’acceptation et aucun cadeau au retour.

| Réponse F06 | Réputation Garde du Port | Impression de Léovic | Argent |
| --- | --- | --- | --- |
| Curiosité | 0 | 0 | 50 Martins conservés |
| Embarras | 0 | 0 | 50 Martins conservés |
| Reconnaissance marquée | +1 | +1 | 50 Martins conservés |
| « Je ne demandais pas votre charité. » | −1 | −1 | 50 Martins conservés |

Les autres gains conditionnels sont conservés : A01-A et T04b-A donnent +1 de réputation et d’impression ; M04-A donne +1 d’impression seulement. Le +5 automatique reste supprimé. La fin conserve les 165 XP, les compétences et les tirages du Port existants ; un tirage peut donner séparément des pièces, distinctes des 50 Martins personnels.

| Situation à la mise à jour | Reprise |
| --- | --- |
| Nouvelle quête ou trajet en cours | Même parcours A–P, mêmes points, mêmes dialogues hors du don. |
| F06/F07 en cours, argent non reçu | Versement unique à la reprise de cette scène, sans rejouer la décision déjà mémorisée. |
| Ancien point F06-REPORTER | Redirection vers F07, versement unique si nécessaire. |
| Argent déjà reçu | Aucun second versement, même si les pièces ont été dépensées. |
| Quête déjà terminée avant HF8 | Réputation et récompenses historiques conservées ; pas de rattrapage automatique à la connexion. L’ancien menu de récupération est supprimé. |

Les 111 identifiants de nœud sont conservés. Deux anciens identifiants deviennent des alias de reprise, sans nouveau dialogue. Les sauvegardes Nexus restent par personnage ; aucun effacement de progression n’est nécessaire.

## Recette sur la carte du serveur

Le rapport distingue les bancs exécutés de cette recette graphique qui reste à faire :

1. Départ de Léovic, pause dans les questions et reprise au même point ; marche sur les obstacles réels jusqu’à M puis P.
2. Entretien à trois, cadrages et maintien du guide pendant les questions ; inscription Visiteur sans abaisser un rang supérieur.
3. F06 : constater les 50 Martins avant réponse ; essayer les quatre réponses sur des personnages distincts, fermer/rouvrir puis recharger. Chaque personnage garde 50 Martins personnels, avec le seul effet social de sa réponse.
4. Terminer au même endroit, sans revenir au poste pour être payé. Vérifier l’offre d’Aurèle puis les retrouvailles facultatives sans option de bourse.
5. Deux joueurs et un observateur : acteurs et choix privés ; changements de personnage Nexus A–B–A. Vérifier le rendu et les FPS avec le modpack réel.
6. PQ01 : Gautier conserve son rythme, Hugues garde ses cinq questions et son congé ; seul son ancien choix 6 reste retiré. MQ03 : Althéon à 10/20/30, retour aux Archives à 40/50, clôture par l’Archivaire puis MQ04.

## Retour arrière

Arrêter le serveur, remettre ensemble les anciens JAR/datapacks et la sauvegarde cohérente du monde et des personnages. Remettre aussi le JAR client correspondant, puis redémarrer complètement.

La carte Indivis et un vrai client graphique ne sont pas chargés dans les bancs livrés. Les tests emploient de vrais Minecraft/Fabric, EasyNPC, Dialogue et fonctions core ; les connexions, la sélection Nexus et les intégrations économiques externes ont les substitutions explicites détaillées dans le rapport.
