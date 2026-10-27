# Indivis — PQ02 R1 : installation et recette

Version ciblée : Minecraft/Fabric **1.21.11**, Java **21**. Base : core **HF9_QMARK2** et journal datapack **HF4_QMARK2** fournis pour cette demande. Le mod de quêtes **HF12** reste en place.

## Fichiers à installer

Décompresser **l’archive de livraison** `Indivis_PQ02_R1_Installation.zip`. Elle contient les deux vrais datapacks et le nouveau mod. Ne pas déposer cette archive de livraison elle-même dans `datapacks`.

| Destination | Fichier fourni | Action |
| --- | --- | --- |
| Serveur : `<monde>/datapacks/` | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1.zip` | Remplacer le core HF9_QMARK2 fourni, y compris s’il était décompressé. Garder une seule copie de ce core. |
| Serveur : `<monde>/datapacks/` | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1.zip` | Remplacer le datapack de journal HF4_QMARK2 fourni. |
| Serveur : `mods/` | `indivis-pq02-1.0.0+1.21.11.jar` | Ajouter ce mod compagnon. |
| **Chaque client** : `mods/` | **Le même** `indivis-pq02-1.0.0+1.21.11.jar` | Ajouter pour la capture, les noms des locuteurs et la lecture manuelle. |

Les deux ZIP internes sont directement utilisables comme datapacks : leur `pack.mcmeta` est à la racine. Ne pas conserver en parallèle leur ancienne version ni un dossier issu de l’ancienne extraction.

Conserver **sans les remplacer** : NexusCharacters PRE35-SI1-OPT1, quêtes HF12, Dialogue b10, EasyNPC 7.16.0, le module RPG et les dépendances déjà fonctionnelles. Nexus NPC PREVIEW1 n’est pas modifié. La monnaie reste `capitale_currency:martin_dor`. Les fonctions CapSkills et les tirages du Port existants restent requis pour leurs récompenses. Aucun de ces JAR inchangés n’est redistribué dans cette livraison.

## Ordre d’installation

1. Arrêter le serveur. Conserver ensemble une sauvegarde du monde, des données des personnages Nexus, des configurations et des anciens fichiers remplacés. Une sauvegarde des seuls datapacks ne permet pas de revenir en arrière sur les personnages qui auront joué.
2. Effectuer les remplacements et ajouter le nouveau mod côté serveur **et clients**.
3. Redémarrer complètement le serveur et les clients. Les mixins du nouveau mod exigent un redémarrage ; un simple `/reload` ne les installe pas.
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

Revenir près du **point A existant** du trajet :

```mcfunction
/function capitale:pq02/admin/preparer_guide_historique
/function capitale:pq02/admin/diagnostic
```

La préparation retire les états d’ancienne escorte du guide armor stand, s’il est chargé, et le stationne au point A existant. Elle ne téléporte pas le PNJ public. Si l’ancien PNJ suit ce guide par sa configuration EasyNPC, son comportement de retour au poste reste celui de cette configuration. Les coordonnées des marqueurs chargés sont mises en cache. Charger les portions du parcours et relancer le diagnostic si des repères sont signalés absents ; aucune coordonnée de remplacement n’est inventée.

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

Arrêter le serveur, retirer `indivis-pq02` des mods serveur et clients, remettre les **deux** datapacks précédents et restaurer la sauvegarde cohérente du monde et des personnages. Le guide historique et les nouvelles données de personnage font partie de cet état ; ne pas restaurer seulement les ZIP après des essais avec des personnages réels. Refaire un démarrage complet.

## Données encore nécessaires à une validation de la carte

La livraison conserve les repères existants. Pour certifier le trajet et les cadrages **sur la carte Indivis**, il faut encore une copie de monde exploitable ou les exports EasyNPC et des marqueurs A–P, ainsi que les configurations réelles de caméra/voix. Les fichiers de monnaie, CapSkills et du module RPG n’étant pas inclus dans le banc complet de dépendances, leurs implémentations tierces n’ont pas été rejouées dans ce banc. Aucun de ces manques n’a été remplacé dans les fichiers livrés par une monnaie ou une récompense fictive.
