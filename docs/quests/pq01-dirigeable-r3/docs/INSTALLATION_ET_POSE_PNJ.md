# Installation, configuration et déplacements des PNJ — PQ01-R3-HF1

**Mise à jour du 23 octobre : journal R3-HF1.** Si les deux datapacks R3 sont déjà installés, ce correctif demande seulement de remplacer le journal par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF1.jar` sur le serveur et les clients, et le mod admin alpha.2 par `indivis-admin-0.1.0-alpha.3+1.21.11.jar` sur le serveur. Redémarrer. Les liaisons et points existants restent utilisables ; les commandes ci-dessous sont inchangées. La section 2 décrit aussi l'installation complète initiale des datapacks R3.

## 1. Les trois opérations à distinguer

| Besoin | Exemple exact | Effet |
| --- | --- | --- |
| Affecter un PNJ existant à son rôle | `/function capitale:pq01/liaison/gautier_large` | Choisit l’EasyNPC le plus proche, sauvegarde puis relie son interaction. Aucun déplacement. |
| Déplacer physiquement ce PNJ en édition | `/function capitale:pq01/pose/placer_gautier_large` | Place le PNJ déjà lié à la position et à l’orientation de l’admin. |
| Définir sa destination de marche | `/function capitale:pq01/pose/gautier_approche` | Enregistre le sol où Gautier doit rejoindre Géraud pendant la scène. |
| Ancien point de journal | `/function capitale:pq01/pose/gautier_large` | Enregistre uniquement un waypoint ; ne lie et ne déplace aucun PNJ. |

**Pendant la quête, Gautier marche réellement vers le point d’approche puis vers son poste.** La commande `placer_…` sert à l’édition du décor ; elle n’est jamais appelée par le trajet de scène. L’acteur temporaire est créé à son poste réel et sa navigation doit rejoindre la destination : aucun TP de scène à travers un mur.

## 2. Remplacer les fichiers

| Emplacement | Fichier à installer |
| --- | --- |
| Datapacks du monde serveur | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3.zip` |
| Datapacks du monde serveur | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3.zip` |
| `mods` serveur et clients | `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF1.jar` |

Les deux ZIP internes sont les datapacks complets, avec `pack.mcmeta` à leur racine. Le ZIP extérieur de livraison n’est pas lui-même un datapack.

1. Fermer les dialogues, attendre la fin d’un amarrage et terminer les relectures actives avec `/function capitale:pq01/admin/test_fin` si R2/R3 est déjà installé. Sauvegarder ensemble le monde et les profils Nexus.
2. Arrêter le serveur. Remplacer les deux datapacks et le JAR journal correspondants par les trois fichiers ci-dessus. Retirer les versions précédentes de ces mêmes composants.
3. Installer aussi le journal R3-HF1 dans les clients. Conserver Nexus `PRE34-SI1`, Dialogue `0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC et son interface `7.16.0`, Fabric et les autres composants du serveur. Aucun remplacement de Nexus ou Dialogue n’est fourni.
4. Redémarrer. Les commandes suivantes s’exécutent en jeu, par un administrateur avec un personnage Nexus sélectionné et chargé.

Le code de scène fonctionne côté serveur ; le JAR journal contient aussi ses composants clients et mixins. Ce correctif s’ajoute au journal fourni, sans créer un second mod portant le même identifiant.

## 3. Préparer l’édition

Suspendre l’ambiance puis nettoyer les acteurs temporaires avant de lier ou déplacer les originaux :

```mcfunction
/data modify storage capitale:pq01_setup ambient_enabled set value 0
/function capitale:pq01/admin/nettoyer_scenes
```

Ces commandes ne réinitialisent aucune quête. Le nettoyage ferme les scènes PQ01 personnelles en cours ; l’effectuer pendant la configuration sur la copie de test du monde. Les liaisons, points et apparences restent enregistrés.

## 4. Lier les six PNJ de chaque exemplaire du navire

Se tenir **à moins de trois blocs du PNJ voulu**, plus près de lui que de tout autre EasyNPC d’au moins 0,5 bloc. Exécuter uniquement la commande de son rôle, puis passer au PNJ suivant. Les exemplaires au large et au quai possèdent des liaisons distinctes ; un même UUID ne peut occuper deux rôles.

| PNJ | Exemplaire au large | Exemplaire au quai |
| --- | --- | --- |
| Géraud Rivet | `/function capitale:pq01/liaison/geraud_large` | `/function capitale:pq01/liaison/geraud_quai` |
| Althéon Brumeforge | `/function capitale:pq01/liaison/altheon_large` | `/function capitale:pq01/liaison/altheon_quai` |
| Gautier | `/function capitale:pq01/liaison/gautier_large` | `/function capitale:pq01/liaison/gautier_quai` |
| Hugues | `/function capitale:pq01/liaison/hugues_large` | `/function capitale:pq01/liaison/hugues_quai` |
| Anselme | `/function capitale:pq01/liaison/anselme_large` | `/function capitale:pq01/liaison/anselme_quai` |
| Perrin | `/function capitale:pq01/liaison/perrin_large` | `/function capitale:pq01/liaison/perrin_quai` |

La liaison conserve le type, le modèle, le skin, les textures, l’équipement et les autres propriétés EasyNPC. Elle attribue le nom approuvé au PNJ et un tag de rôle si absent ; avant toute écriture, elle sauvegarde son UUID, son interaction, son éventuel événement near, son ancien nom et le fait que le tag était déjà présent. Les autres événements restent en place.

`ON_INTERACTION` doit être absent ou contenir une seule ancienne commande connue d’un de ces six membres d’équipage. Une interaction multiple/personnalisée est refusée, avec message explicite ; elle n’est pas écrasée. Aucun refus ne demande de changer de datapack. Une liaison existante vers un autre PNJ doit être libérée avant réaffectation.

L’interaction COMMAND est exécutée comme le joueur, avec permission 2. Pour Géraud/Althéon au large, un `ON_DISTANCE_NEAR` absent reçoit le hook existant du prologue. Un near déjà configuré est préservé : vérifier sa portée et son activation dans EasyNPC. La liaison ne remplace pas les paramètres de détection propres au monde. Le point de waypoint du rôle est également enregistré à la position du PNJ.

Un PNJ impliqué dans une scène personnelle ou occupé par un autre joueur ne peut pas être lié/libéré/déplacé tant que cette utilisation dure.

## 5. Déplacer les originaux si leur poste doit changer

Après liaison, se placer au poste réel souhaité, pieds sur un sol accessible et regard dans le bon sens, puis exécuter la commande individuelle. Le PNJ doit être chargé dans la même dimension, à 64 blocs maximum. La commande déplace uniquement le PNJ lié, puis actualise son poste sauvegardé et son waypoint. Aucune conversation ne doit être active pour l’admin.

| PNJ | Au large | Au quai |
| --- | --- | --- |
| Géraud Rivet | `/function capitale:pq01/pose/placer_geraud_large` | `/function capitale:pq01/pose/placer_geraud_quai` |
| Althéon Brumeforge | `/function capitale:pq01/pose/placer_altheon_large` | `/function capitale:pq01/pose/placer_altheon_quai` |
| Gautier | `/function capitale:pq01/pose/placer_gautier_large` | `/function capitale:pq01/pose/placer_gautier_quai` |
| Hugues | `/function capitale:pq01/pose/placer_hugues_large` | `/function capitale:pq01/pose/placer_hugues_quai` |
| Anselme | `/function capitale:pq01/pose/placer_anselme_large` | `/function capitale:pq01/pose/placer_anselme_quai` |
| Perrin | `/function capitale:pq01/pose/placer_perrin_large` | `/function capitale:pq01/pose/placer_perrin_quai` |

Exemples de placement : Gautier à son poste de surveillance, Hugues près de la cargaison, Anselme devant les instruments et Perrin au départ de son service. Les modèles et équipements existants restent utilisés ; aucune apparence nouvelle n’est imposée. Ne pas lancer toutes les commandes depuis un même endroit.

## 6. Définir l’aller/retour de Gautier et les occupations au large

Chaque commande ci-dessous enregistre **la position actuelle de l’admin**, sans déplacer immédiatement le PNJ.

| Commande | Se placer à cet endroit |
| --- | --- |
| `/function capitale:pq01/pose/gautier_poste` | Poste de surveillance de Gautier, sur le pont du navire au large. |
| `/function capitale:pq01/pose/gautier_approche` | Point d’arrêt sûr près de Géraud, à quatre blocs maximum de lui. |
| `/function capitale:pq01/pose/hugues_charge` | Point vers lequel Hugues regarde pendant son inspection de cargaison. |
| `/function capitale:pq01/pose/anselme_instruments` | Instruments ou barre vers lesquels Anselme regarde. |
| `/function capitale:pq01/pose/perrin_a` | Premier point du service léger de Perrin. |
| `/function capitale:pq01/pose/perrin_b` | Second point du service léger de Perrin. |

Gautier : poste et approche dans la même dimension, sur un sol praticable, à 24 blocs maximum l’un de l’autre et proches des PNJ sources. Laisser une voie réelle entre eux, compatible avec les portes et escaliers du décor. Les acteurs n’ouvrent pas les portes ; un trajet fermé produit un repli explicite. Perrin : deux points accessibles dans la même zone, à 24 blocs maximum. Aucun chemin ni coordonnée du décor n’a été inventé.

Gautier attend son arrivée pour prononcer son interruption. Après « Bien, quartier-maître ! » et sa durée audio éventuelle, il rejoint réellement son poste, puis Géraud revient vers le joueur. Hugues regarde la cargaison et fait un geste d’inspection périodique sans modifier les inventaires. Anselme regarde ses instruments. Perrin marche entre A et B, avec une pause aux extrémités. Leur occupation se suspend pendant leur propre dialogue ; la scène principale garde la priorité.

## 7. Points d’activité au quai

Les points du navire au large ne sont **jamais réutilisés comme destinations sur le navire amarré**. Sur cet autre exemplaire, on peut enregistrer séparément :

```mcfunction
/function capitale:pq01/pose/gautier_poste_quai
/function capitale:pq01/pose/hugues_charge_quai
/function capitale:pq01/pose/anselme_instruments_quai
/function capitale:pq01/pose/perrin_a_quai
/function capitale:pq01/pose/perrin_b_quai
```

Appliquer les mêmes consignes de position et de portée. Sans ces points facultatifs du quai, les PNJ restent à leur poste source ; Perrin n’entame pas de ronde non configurée. Il n’existe pas de seconde interruption obligatoire de Gautier au quai.

## 8. Réactiver puis contrôler

```mcfunction
/data modify storage capitale:pq01_setup ambient_enabled set value 1
/function capitale:pq01/admin/status
```

Le statut distingue liaison manquante, configurée mais non chargée dans la dimension, ou chargée ; il liste les points enregistrés. Les acteurs d’ambiance apparaissent seulement pour un personnage dont PQ01 est actif, à proximité du navire ; ils sont retirés à la fermeture, à l’éloignement ou à la perte du contexte. Les PNJ originaux restent à leur poste.

Échange Hugues/Gautier facultatif, désactivé par défaut :

```mcfunction
/data modify storage capitale:pq01_config allow_banter set value 1
```

Il est proposé via le menu d’Hugues, au large uniquement. Il exige les deux acteurs personnels à huit blocs maximum, aucun autre joueur à seize blocs et un cooldown expiré de 1 200 ticks en ligne. Il ne déplace aucun original et ne progresse aucune quête. Remettre `allow_banter` à `0` pour le désactiver.

## 9. Libérer une liaison et restaurer les actions sauvegardées

Se tenir à huit blocs maximum du PNJ lié, dans sa dimension, après fermeture des scènes. Choisir la seule commande correspondante :

```mcfunction
/function capitale:pq01/liaison/liberer_geraud_large
/function capitale:pq01/liaison/liberer_altheon_large
/function capitale:pq01/liaison/liberer_gautier_large
/function capitale:pq01/liaison/liberer_hugues_large
/function capitale:pq01/liaison/liberer_anselme_large
/function capitale:pq01/liaison/liberer_perrin_large
/function capitale:pq01/liaison/liberer_geraud_quai
/function capitale:pq01/liaison/liberer_altheon_quai
/function capitale:pq01/liaison/liberer_gautier_quai
/function capitale:pq01/liaison/liberer_hugues_quai
/function capitale:pq01/liaison/liberer_anselme_quai
/function capitale:pq01/liaison/liberer_perrin_quai
```

La restauration concerne seulement les champs modifiés par la liaison. Si une action a été modifiée entre-temps dans EasyNPC, la restauration est refusée pour ne pas écraser ce travail. Les changements cosmétiques ultérieurs sont conservés ; le nom est restauré seulement s’il est encore celui posé par la liaison. Le placement physique d’édition n’est pas annulé par cette commande.

## 10. Paramètres et dépannage ciblé

Les données de configuration sont dans `capitale:pq01_setup` : `bindings`, `points`, `ambient_enabled` (1 par défaut) et `move_timeout_ticks` (300 par défaut, borné à 20–1 200). Pour modifier le délai :

```mcfunction
/data modify storage capitale:pq01_setup move_timeout_ticks set value 300
```

Un acteur absent, un point non configuré, un chemin refusé ou le dépassement du délai déclenche un message de raison et un repli qui laisse la quête avancer. Le repli supprime l’acteur personnel concerné ; aucun PNJ original n’est téléporté pour forcer l’animation. Après correction du chemin, un admin peut réarmer uniquement l’introduction de son personnage :

```mcfunction
/function capitale:pq01/admin/fermer_dialogue
/function capitale:pq01/admin/reparer_scene_gautier
```

Cette réparation est limitée à PQ01 avant l’entretien achevé du capitaine. Pour revoir PQ01 sur un personnage plus avancé, employer `test_debut`/`test_fin` décrits dans `TESTS_EN_JEU.md`.

En cas de « unknown function », les adresses ci-dessus correspondent aux fichiers présents dans les ZIP R3 et sont contrôlées dans la livraison. Le journal R3 fournit la commande native `indivispq01` : son absence ou une erreur de chargement se diagnostique dans le log de démarrage, sans reset de profil ni vérification générique des datapacks.
