# Indivis — PQ02 R1 HF1 : installation et recette

Version ciblée : Minecraft/Fabric **1.21.11**, Java **21**. Base : core **HF9_QMARK2** et journal datapack **HF4_QMARK2** fournis pour cette demande. Le mod de quêtes **HF12 est remplacé par sa version intégrant PQ02**, sans compagnon séparé.

## Fichiers à installer

Décompresser **l’archive de livraison** `Indivis_PQ02_R1_HF1_Installation_Integree.zip`. Elle contient les deux datapacks et le mod de quêtes fusionné. Ne pas déposer cette archive de livraison elle-même dans `datapacks`.

| Destination | Fichier fourni | Action |
| --- | --- | --- |
| Serveur : `<monde>/datapacks/` | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF1.zip` | Remplacer le core précédent, y compris s’il était décompressé. Garder une seule copie de ce core. |
| Serveur : `<monde>/datapacks/` | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1.zip` | Remplacer le datapack de journal HF4_QMARK2 fourni. |
| Serveur : `mods/` | `haute-capitale-quetes-HF12-PQ02-R1-HF1-1.21.11.jar` | Remplacer l’ancien JAR de quêtes et retirer le compagnon `indivis-pq02` s’il a été installé. |
| **Chaque client** : `mods/` | **Le même** `haute-capitale-quetes-HF12-PQ02-R1-HF1-1.21.11.jar` | Remplacer l’ancien JAR de quêtes et retirer `indivis-pq02`, comme sur le serveur. |

Les deux ZIP internes sont directement utilisables comme datapacks : leur `pack.mcmeta` est à la racine. Ne pas conserver en parallèle leur ancienne version ni un dossier issu de l’ancienne extraction.

Conserver **sans les remplacer** : NexusCharacters PRE35-SI1-OPT1, Dialogue b10, EasyNPC 7.16.0, le module RPG et les dépendances déjà fonctionnelles. Nexus NPC PREVIEW1 n’est pas modifié. La monnaie reste `capitale_currency:martin_dor`. Les fonctions CapSkills et les tirages du Port existants restent requis pour leurs récompenses. Les JAR de ces autres modules ne sont pas redistribués dans cette livraison.

## Si la première livraison PQ02 R1 est déjà installée

Remplacer le **core R1 par le core R1 HF1 côté serveur**, puis le JAR de quêtes par le **seul** JAR fusionné HF1 sur le serveur et chaque client. Le datapack **`hc_quetes` R1 est inchangé** : le conserver s’il est déjà installé. Retirer `indivis-pq02` s’il est encore présent. Les PNJ, les liaisons et la progression enregistrée sont conservés ; aucune réinitialisation ni réinstallation des personnages n’est nécessaire. Redémarrer complètement.

Le JAR garde l’identifiant `haute_capitale_quetes`, les mêmes configurations et données. Fabric refuse explicitement sa coexistence avec l’ancien compagnon `indivis_pq02` pour éviter deux moteurs PQ02 simultanés.

## Ordre d’installation

1. Arrêter le serveur. Conserver ensemble une sauvegarde du monde, des données des personnages Nexus, des configurations et des anciens fichiers remplacés. Une sauvegarde des seuls datapacks ne permet pas de revenir en arrière sur les personnages qui auront joué.
2. Installer les deux datapacks et remplacer le mod de quêtes côté serveur **et clients**, et retirer le compagnon PQ02.
3. Redémarrer complètement le serveur et les clients. Les mixins du mod de quêtes fusionné exigent un redémarrage ; un simple `/reload` ne les installe pas.
4. Préparer les liaisons ci-dessous avec un administrateur. Utiliser les PNJ et les marqueurs **déjà présents**, sans réinstaller PQ01 ou la séquence I.

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

## Migration conservatrice

| Personnage existant | Comportement |
| --- | --- |
| N’a pas commencé PQ02 | Parcours complet et quatre décisions nouvelles. |
| A déjà accepté l’escorte | Étape canonique conservée, décisions passées inconnues verrouillées sans bonus inventé ; reprise en pause sur le trajet existant. La première reprise d’un ancien dossier ne dispose pas d’une position personnelle antérieure et se repère sur les marqueurs proches. |
| Est déjà inscrit | Rang et inscription conservés ; aucun nouvel effet d’inscription. |
| A terminé PQ02 | Aucun retrait rétroactif du +5 ancien, aucune seconde récompense de fin. Impression initiale neutre puisque les choix historiques sont inconnus. |
| A déjà reçu le don | `CAP_LEOVIC_GIFT` conservé ; aucun autre don personnel de 50 MDI. |
| A terminé sans recevoir le don | Possibilité de demander le don au retour, une seule fois, sans modifier réputation ni impression. |

Les nouveaux scores `CAP_PQ2_*` et l’ancien `CAP_LEOVIC_GIFT` sont pris en charge par la politique réelle PRE35 de Nexus. Le trigger, les jetons et les sessions restent transitoires. Les positions de scène sont indexées par UUID de personnage, avec contrôle du propriétaire. Le test automatisé des changements de personnages utilise une sélection et une restauration synthétiques ; la sélection graphique Nexus doit encore être essayée sur le serveur cible.

## Recette sur une copie du serveur

1. Un personnage ayant terminé PQ01 : poser les questions d’accueil dans plusieurs ordres ; fermer et rouvrir ; accepter l’escorte.
2. Poser des questions pendant la marche et attendre une minute dans un sous-menu : guide immobile, texte conservé, reprise au même point.
3. Traverser le trajet réel : marches, dalles, portes éventuelles, arrivée au poste ouvert du magistrat ; Léovic reste présent dans tout l’entretien. Vérifier les cadrages L/M/3 sur un vrai client.
4. Inscrire un personnage étranger : Visiteur minimum. Refaire le test avec un rang supérieur à 10 : aucune rétrogradation. Puis suivre les quelques pas M–P.
5. Vérifier gratitude, curiosité, refus poli et mépris sur des personnages de test distincts. Achever, revenir au Léovic permanent et accepter un don différé : **50 MDI une fois**, aucun nouvel effet de réputation. Les tirages aléatoires existants peuvent ajouter séparément des pièces ; ils ne constituent pas un second don de 50.
6. Deux joueurs en même temps, puis un troisième observateur : chacun voit sa scène, ses caméras et ses choix. Déconnecter un joueur en marche ; l’autre continue normalement. Tester deux personnages Nexus d’un même compte, puis revenir au premier.
7. Hugues : son accroche et ses cinq questions restent là ; le choix 6 a disparu ; le congé conserve son numéro 7. Ne pas réécrire ou réinstaller les autres scènes PQ01.
8. MQ03 : Althéon ouvre les retrouvailles à 10, le journal de bord à 20 et la consultation à 30 ; le retour aux Archives passe à 40, puis 50 auprès des Sentinelles ; l’Archivaire clôt à 100 et ouvre MQ04 à 10. Vérifier avec un second personnage n’étant pas à cette étape. Les critères MQ01 restent prologue terminé, Résident minimum et `CAP_POINTS_TOTAL >= 5`.

## Retour arrière

Arrêter le serveur, retirer le JAR fusionné des mods serveur et clients, remettre le JAR de quêtes et les **deux** datapacks de la sauvegarde et restaurer la sauvegarde cohérente du monde et des personnages. Le guide historique et les nouvelles données de personnage font partie de cet état ; ne pas restaurer seulement les ZIP après des essais avec des personnages réels. Refaire un démarrage complet.

## Données encore nécessaires à une validation de la carte

La livraison conserve les repères existants. Pour certifier le trajet et les cadrages **sur la carte Indivis**, il faut encore une copie de monde exploitable ou les exports EasyNPC et des marqueurs A–P, ainsi que les configurations réelles de caméra/voix. Les fichiers de monnaie, CapSkills et du module RPG n’étant pas inclus dans le banc complet de dépendances, leurs implémentations tierces n’ont pas été rejouées dans ce banc. Aucun de ces manques n’a été remplacé dans les fichiers livrés par une monnaie ou une récompense fictive.
