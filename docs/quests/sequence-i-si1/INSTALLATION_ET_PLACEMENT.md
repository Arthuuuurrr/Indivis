# Indivis — Installation et placement de la Séquence I — SI1b

Version du guide : 20 octobre 2026. Ce document regroupe toute la configuration de placement pour SI1b et remplace le précédent guide SI1/SI1a.

Le contenu livré couvre **MQ01 — La Convocation**, **MQ02 — Les Registres**, **MQ03 — Le Journal de bord**, puis **l'ouverture de MQ04 — Havre-Fort et son premier objectif au port**. La suite maritime de MQ04 n'est pas incluse dans cette version.

## 1. Fichiers à utiliser

Si SI1 ou SI1a est déjà installé, seuls ces deux fichiers changent :

| Fichier SI1b | Destination |
|---|---|
| `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b.jar` | Dossier `mods` du serveur **et des clients** |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b.zip` | Dossier `datapacks` du monde sur le serveur |

Arrêter le serveur et les clients concernés, sauvegarder le monde et les profils Nexus, puis remplacer les anciennes variantes du journal et du pack de quêtes. Ne laisser qu'une version active de chacun. Redémarrer complètement pour charger le JAR.

Conserver le core `capitale_core_1.5.7_V2_SEQUENCE_I_SI1.zip` et `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar` déjà fournis avec SI1 : ils sont identiques dans SI1b. Les deux fichiers SI1b sont une mise à jour du lot Séquence I ; ils supposent cette base SI1 déjà présente. Aucun reset de progression n'est requis.

Le mod admin est indépendant de ce placement. L'environnement reste Minecraft **1.21.11**, avec les dépendances déjà utilisées pour SI1, notamment EasyNPC **7.16.0** et son interface de configuration.

**Si une liaison affiche « Unknown function ».** Une fonction présente dans le datapack peut être refusée au chargement si une commande du mod journal manque. Le démarrage serveur transmis le 20 octobre confirme le chargement de `hc_quetes_b22_V2_SEQUENCE_I_SI1b.zip`, mais déclare le journal `0.1.0+1.21.11.b7.SEQUENCE-I.SI1a`. Cette version n'enregistre pas `indivissequence guardlink1` ni `guardlink2` ; Minecraft refuse alors les fonctions de liaison et de restauration. Dans ce cas précis, conserver le datapack SI1b et remplacer le JAR du journal par celui indiqué ci-dessus, puis redémarrer complètement le serveur et les clients concernés. Le journal doit déclarer `0.1.0+1.21.11.b7.SEQUENCE-I.SI1b` au démarrage ; le nom du fichier JAR seul ne prouve pas sa version. Les commandes de placement restent identiques.

## 2. Règle commune aux commandes de placement

Exécuter les commandes **dans le jeu avec un compte administrateur**, dans la dimension et au poste concernés, avec le chunk chargé. La position enregistrée est celle du joueur ou du PNJ ciblé selon l'action ; il faut donc se déplacer entre les étapes.

Les deux gardes des Archives sont déjà présents : **les lier individuellement**, sans capture de modèle préalable. Les seules créations nécessaires sont le Scribe et l'Archivaire, s'ils n'ont pas déjà été posés. Althéon est lui aussi lié à son entité existante.

## 3. Lier les deux gardes existants des Archives

S'approcher à **trois blocs au plus du premier garde des Archives**, nettement plus près de lui que du second, puis exécuter :

```mcfunction
/function hc_quetes:liaison/sentinelle_archives_1
```

Aller ensuite près du **deuxième garde**, à trois blocs au plus, puis exécuter :

```mcfunction
/function hc_quetes:liaison/sentinelle_archives_2
```

Les numéros 1 et 2 correspondent à deux postes distincts : un même garde ne peut pas occuper les deux. Si deux gardes admissibles sont à une distance trop similaire, la liaison est refusée ; se rapprocher davantage du garde voulu et recommencer. Attendre le message de réussite pour chaque poste.

La liaison sauvegarde les actions avant modification et raccorde l'interaction au dialogue de quête. L'apparence, l'équipement, le poste, les contrôles de proximité et les réactions à l'agression sont conservés. Les interactions judiciaires restent prioritaires à partir de `CAP_CRIME = 10`.

Pour examiner les gardes proches en cas de refus, à huit blocs au plus :

```mcfunction
/function hc_quetes:pose/inspecter_garde
```

Cette inspection ne modifie pas les gardes. Si la liaison signale une interaction différente du modèle vérifié ou un poste déjà enregistré, conserver le message pour le diagnostic. Les commandes historiques de capture et de création des Sentinelles ne sont pas les étapes à utiliser pour ces gardes existants.

## 4. Placer le Scribe des Archives

Se tenir à l'emplacement exact voulu pour le Scribe, dans **la grande salle des Archives sous le dôme**, au niveau de son bureau, en regardant dans la direction que le PNJ devra regarder :

```mcfunction
/function hc_quetes:pose/pnj/scribe_archives
```

La commande crée le Scribe à votre position et enregistre sa cible pour le journal. Si le Scribe SI1 est déjà placé, ne pas le recréer ; utiliser la procédure de déplacement plus bas uniquement si son poste doit changer.

## 5. Placer l'Archivaire de la Couronne

Se tenir à son poste, près de **la table de lecture dans la même salle**, avec l'orientation voulue :

```mcfunction
/function hc_quetes:pose/pnj/archivaire_couronne
```

La commande crée l'Archivaire et enregistre sa cible. Comme pour le Scribe, conserver le PNJ SI1 déjà posé si son emplacement convient. Les apparences de ces nouveaux PNJ restent réglables dans EasyNPC par Clément.

## 6. Lier Althéon existant

Aller près d'**Althéon Brumeforge**, à trois blocs au plus. Veiller à ce qu'aucun autre PNJ EasyNPC humanoïde ne soit plus proche, car cette commande choisit le plus proche :

```mcfunction
/function hc_quetes:liaison/altheon
```

La commande enregistre Althéon pour la Séquence I en conservant son apparence et ses actions. Elle ne crée pas de deuxième Althéon.

## 7. Délimiter le volume des Archives

Le contrôle de sortie utilise un **volume en trois dimensions**. Choisir deux coins opposés qui englobent toutes les salles et tous les bureaux visités pendant les quêtes des Archives. L'espace extérieur après la sortie doit se trouver hors de ce volume.

Se placer au premier coin, près du niveau inférieur du volume :

```mcfunction
/function hc_quetes:pose/zone/archives_coin_a
```

Se placer au coin opposé, en hauteur et à l'autre extrémité du bâtiment :

```mcfunction
/function hc_quetes:pose/zone/archives_coin_b
```

Les deux coins doivent être dans **la même dimension**, avec au moins un bloc d'écart sur **chacun des trois axes X, Y et Z**. Deux coins au même niveau Y produisent un volume invalide. Prévoir une marge autour des déplacements possibles du joueur dans les salles.

Pour corriger un coin, revenir à sa nouvelle position et relancer sa commande. Ces commandes enregistrent une zone ; elles ne placent aucun bloc.

## 8. Enregistrer le port de Havre-Fort

Se rendre au **point réel d'arrivée souhaité dans le port de Havre-Fort**, puis exécuter :

```mcfunction
/function hc_quetes:pose/zone/havre_fort_port
```

Ce point sert de cible au journal et à Xaero pour l'ouverture de MQ04. Le premier objectif se valide dans un rayon de **24 blocs** autour de cette position. La dimension est enregistrée. Pour déplacer la cible, relancer la même commande au nouveau point.

## 9. Vérifier que le placement est prêt

Une fois les deux gardes liés, le Scribe et l'Archivaire posés, Althéon lié, les deux coins définis et le port enregistré :

```mcfunction
/function hc_quetes:admin/sequence_i/status
```

Le résultat attendu est : **« Configuration de placement complète. »**

Si le résultat est **« Configuration de placement incomplète : le messager reste en attente. »**, reprendre les étapes concernées : cibles des PNJ, liaison d'Althéon, deux coins dans la même dimension avec un volume valide, puis point du port.

Le statut vérifie les cibles et le volume enregistrés. Les deux gardes partagent une cible de journal : vérifier aussi que **chacune des deux liaisons a bien renvoyé son message de réussite**. Le message global ne remplace pas ce contrôle individuel.

## 10. Déclenchement automatique du messager

Le messager n'a pas de poste à placer manuellement. Il attend la configuration complète, puis apparaît près du personnage éligible dans une ville impériale admissible, en conservant les biomes de ville prévus pour ces zones sûres.

Pour tester le début de MQ01, utiliser un personnage Nexus actif et déverrouillé qui remplit les conditions suivantes :

- PQ05 terminée : `hcq_pq05 = 100`.
- Rang Résident ou supérieur : `CAP_RANGSOCIAL >= 20`.
- Au moins **5 points CapSkills cumulés** : `CAP_POINTS_TOTAL >= 5` avec le seuil livré. Il s'agit des points acquis ; les dépenser ne retire pas cette éligibilité.
- MQ01 pas encore commencée, dans un biome impérial admissible.
- Hors combat récent, sans feu, sans hostile à douze blocs et avec `CAP_CRIME < 30`.
- Un emplacement proche avec un sol admissible et deux blocs d'air au-dessus, ainsi qu'une place d'inventaire pour recevoir la convocation.

La citoyenneté n'est pas requise. Si la zone proche est encombrée, se déplacer sur un espace dégagé de la ville et laisser le système réessayer. Un inventaire plein bloque la remise de la lettre sans faire avancer la quête.

Le messager et ses marqueurs sont propres au compte et au personnage actif. Suivre ensuite les dialogues et objectifs dans l'ordre : MQ01, MQ02, MQ03, puis départ vers le port pour le premier objectif MQ04. La convocation est nominative ; les informations de MQ03 sont conservées dans le journal.

## 11. Contrôles en jeu après placement

1. Interagir séparément avec les deux gardes liés et vérifier leurs réactions de proximité et d'agression.
2. Avec un personnage éligible, obtenir la convocation puis suivre les dialogues du Scribe et de l'Archivaire et la rencontre avec Althéon, selon les objectifs affichés.
3. Vérifier que la sortie des Archives est reconnue à la sortie réelle du bâtiment, et non au milieu d'une salle.
4. Sélectionner la quête principale dans le journal si une autre quête est déjà suivie. Vérifier que les cibles correspondent aux postes enregistrés, puis rejoindre le point du port pour le premier objectif MQ04.
5. Reconnecter le personnage et vérifier que sa progression est conservée. Avec un autre personnage, contrôler que sa progression et son messager restent distincts.

Les chemins des commandes de ce guide ont été vérifiés dans le datapack SI1b livré. La validation complète de SI1b dans Minecraft/Fabric reste à effectuer ; les contrôles de fichiers ne constituent pas une recette en jeu.

## 12. Restaurer ou déplacer un garde existant

Pour retirer sa liaison, revenir **à huit blocs au plus du garde**, dans sa dimension, avec son chunk chargé. Exécuter la commande correspondant à son poste :

```mcfunction
/function hc_quetes:liaison/liberer_sentinelle_archives_1
```

Ou, près du deuxième garde :

```mcfunction
/function hc_quetes:liaison/liberer_sentinelle_archives_2
```

Les actions d'origine sont restaurées et le garde reste présent. Faire cette restauration avant de retirer SI1b ou de réaffecter le poste. Si ses actions ont été modifiées depuis la liaison, la restauration refuse de les écraser et conserve la sauvegarde : conserver ce message pour le diagnostic.

Après une restauration réussie, une nouvelle liaison peut être effectuée au poste voulu. Le déplacement physique d'un garde du core et l'ajustement de ses ancrages de contrôle restent à réaliser avec ses outils habituels ; la commande de liaison ne déplace pas le garde.

## 13. Déplacer le Scribe ou l'Archivaire

Revenir au poste initial, dans la bonne dimension, avec le PNJ chargé. Libérer uniquement le poste concerné :

```mcfunction
/function hc_quetes:pose/liberer/scribe_archives
```

Ou :

```mcfunction
/function hc_quetes:pose/liberer/archivaire_couronne
```

Ces commandes suppriment le PNJ créé par SI1 pour ce rôle et libèrent son emplacement enregistré. Se rendre ensuite au nouveau poste et relancer sa commande de pose de l'étape 4 ou 5. Une deuxième pose sans libération est refusée ; si le PNJ a déjà été supprimé, revenir à huit blocs au plus de son ancien poste pour permettre sa repose.

Après un déplacement, ajuster les coins des Archives si nécessaire, puis relancer la vérification de l'étape 9.
