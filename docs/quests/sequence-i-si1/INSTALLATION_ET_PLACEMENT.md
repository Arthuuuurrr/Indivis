# Installation et placement individuel

## Mise à jour et gardes existants — SI1b

Lire `CORRECTIF_SI1b.md`. Remplacer le journal et le pack de quêtes seulement. Lier les gardes déjà présents à moins de trois blocs, un par un. La capture est réservée à la création depuis un modèle lorsque le poste est réellement vide. Aucun reset de progression.

## Première installation : versions et fichiers

Utiliser une copie complète du serveur avec Minecraft **1.21.11**, Java **21 ou plus**,
Fabric Loader **0.19.5 ou plus** et Fabric API **0.141.6 ou plus** (exigences du Nexus fourni).
Conserver EasyNPC **7.16.0**, son interface de configuration, Pufferfish Skills **0.17.3**,
Attributes **0.8.2**, CapSkills et les ressources actuellement utilisés.

Installer les deux ZIP de `installation/datapacks/` dans le dossier `datapacks` du monde de test.
Remplacer les JAR du journal b6 et de Nexus PRE34 par les deux JAR SI1 de `installation/mods/`.
Installer les mêmes deux JAR côté clients ; conserver les autres mods, notamment le dialogue et Xaero.
Retirer les anciennes variantes de ces deux datapacks et deux mods de leurs dossiers actifs.
Le redémarrage complet est nécessaire pour les JAR. Aucun fichier du serveur n'est modifié par cette livraison.

L'installateur optionnel affiche le plan avant toute écriture :

```bash
python3 tools/install_candidate.py --mods-dir /chemin/serveur/mods --datapacks-dir /chemin/serveur/monde/datapacks
```

Après arrêt et sauvegarde de la copie de test, répéter avec `--apply`. Côté client, fournir seulement
`--mods-dir`. Le script repère les mods par leur identifiant Fabric, et les datapacks ZIP/dossiers
par leurs fonctions de chargement. Les anciens fichiers sont déplacés vers des dossiers de sauvegarde
adjacents ; les autres fichiers restent en place. Lire le plan si un pack utilise une arborescence inhabituelle.

## Poses sur la carte

Chaque fonction ci-dessous s'exécute **dans le jeu**, à la position et dans la dimension voulues.
Les permissions administrateur sont nécessaires. Poser chaque personnage à l'intérieur d'un chunk chargé.

| Action | Commande |
|---|---|
| Lier le premier garde des Archives existant | `/function hc_quetes:liaison/sentinelle_archives_1` |
| Lier le deuxième garde des Archives existant | `/function hc_quetes:liaison/sentinelle_archives_2` |
| Examiner les gardes proches | `/function hc_quetes:pose/inspecter_garde` |
| Capturer un modèle, à huit blocs au plus d'un garde du Cœur statique opérationnel | `/function hc_quetes:pose/reference_garde` |
| Première Sentinelle, à son poste | `/function hc_quetes:pose/pnj/sentinelle_archives_1` |
| Deuxième Sentinelle, à son poste | `/function hc_quetes:pose/pnj/sentinelle_archives_2` |
| Scribe, à son bureau | `/function hc_quetes:pose/pnj/scribe_archives` |
| Archivaire, à son bureau | `/function hc_quetes:pose/pnj/archivaire_couronne` |
| Lier Althéon existant, à moins de 3 blocs et sans autre EasyNPC plus proche | `/function hc_quetes:liaison/altheon` |
| Premier coin du volume des Archives | `/function hc_quetes:pose/zone/archives_coin_a` |
| Coin opposé du volume des Archives | `/function hc_quetes:pose/zone/archives_coin_b` |
| Point d'arrivée au port réel de Havre-Fort | `/function hc_quetes:pose/zone/havre_fort_port` |
| État de préparation du placement | `/function hc_quetes:admin/sequence_i/status` |

La capture du garde conserve son type, ses réglages EasyNPC, équipements, apparence et actions.
Elle ne modifie pas le garde source. Le clone reçoit une nouvelle identité et un ancrage propre ;
ses chemins de contrôle d'accès sont raccordés aux Archives, et son interaction au dialogue MQ.
Une capture invalide bloque la création. Vérifier en jeu le clone face au garde source avant de valider la recette.
Les nouveaux PNJ ne reçoivent aucun skin inventé ; Clément peut ajuster leurs apparences.

Althéon conserve ses actions et son apparence ; son point d'entrée existant est enrichi seulement pour MQ03.
Les deux coins doivent englober les salles et bureaux visités, sur les trois axes, dans une même dimension,
avec la sortie hors du volume. Un coin près du sol et le coin opposé en hauteur évitent un volume plat.
Le point du port valide le premier objectif MQ04 dans un rayon de 24 blocs ; aucune cible d'île n'est ajoutée.

Le messager attend que les quatre cibles PNJ, les deux coins et le port soient configurés. Il apparaît ensuite
près des personnages éligibles, dans les biomes impériaux conservés du core, sur un sol admissible,
avec deux blocs d'air, hors combat récent, feu, criminalité active et monstres proches.

## Restaurer un garde existant

Avant de retirer SI1b ou déplacer un garde lié, revenir près de lui et exécuter sa fonction `hc_quetes:liaison/liberer_sentinelle_archives_1` ou `_2`. Les actions d’origine sont restaurées sans supprimer le garde. Voir les refus et sauvegardes dans `CORRECTIF_SI1b.md`.

## Reposer ou déplacer un nouveau PNJ

Une deuxième pose est refusée si son entité est présente. Un emplacement enregistré empêche aussi une pose
lointaine quand le chunk initial est déchargé. Après suppression du seul PNJ concerné, sa fonction de pose
fonctionne de nouveau à moins de huit blocs de son ancien poste, dans sa dimension.

Pour un déplacement volontaire, revenir au poste initial et exécuter sa libération :
`/function hc_quetes:pose/liberer/scribe_archives`, `archivaire_couronne`, `sentinelle_1` ou `sentinelle_2`.
Puis se rendre au nouveau poste et relancer la pose. Ces commandes ciblent uniquement les nouvelles entités SI1.
Après un déplacement de Sentinelle, contrôler ses ancrages avec la procédure de recette.

## Déclenchement et suivi

MQ01 exige PQ05 terminée (`hcq_pq05=100`), rang Résident au moins (`CAP_RANGSOCIAL>=20`),
cinq points CapSkills cumulés et une ville impériale admissible. La citoyenneté n'est pas exigée.
`CAP_POINTS_TOTAL` conserve le maximum du total accordé par la catégorie `capskills:doctrine` ;
dépenser ses points ne réduit donc pas l'éligibilité. Le seuil se règle par
`/scoreboard players set #points hcm_cfg 5` ; un rechargement remet la valeur de livraison à 5.

La convocation est un livre lisible à modèle d'icône `minecraft:paper`, au nom RP Nexus actif.
Le journal conserve les faits de MQ03 : aucun second livre de navigation n'est distribué.
L'enregistrement de remise permet de revenir si le pli a été perdu. Un inventaire plein bloque la remise sans avancer.

Les étapes entrent dans le journal existant ; le suivi automatique de MQ respecte une autre quête déjà suivie.
Le joueur peut choisir la MQ dans son journal. Les cibles Xaero utilisent les positions réellement enregistrées,
y compris dans une autre dimension et lorsque le PNJ n'est pas chargé.
Les marqueurs et le messager sont filtrés pour le compte **et** le personnage actif.

Les boutons MQ portent un numéro de session ; changement de personnage, fermeture et reconnexion
invalident les anciennes conversations. Une fermeture, un éloignement ou l'expiration à 60 secondes
libère la pause du timer d'accès des Archives. Les autres timers judiciaires continuent normalement.
