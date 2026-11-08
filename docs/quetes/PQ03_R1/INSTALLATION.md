# PQ03 R1 HF1 — Installation

Version du 8 novembre 2026, pour Minecraft **1.21.11 Fabric**, sur la base core HF8 + journal **Fusion C1** + mod de quêtes **PQ02 HF9** fournie. Il s’agit d’une livraison à installer et à recetter sur la carte Indivis ; aucun déploiement sur le serveur n’a été effectué.

## Mise à jour depuis PQ03 R1

Remplacer seulement le core par HF1 et le JAR de quêtes sur serveur **et clients**, puis redémarrer complètement. Le journal R1 reste strictement identique. **Aucune préparation, liaison, commande de placement ou remise à zéro n’est à refaire.** Les choix de réputation passent en application immédiate ; la migration des décisions déjà mémorisées est automatique.

Les étapes de préparation plus bas concernent une **première installation**, si elles n’ont jamais été faites. Le détail des gains immédiats et de leur notification se trouve dans `RAPPORT_PQ03_R1_HF1.md`.

## Fichiers à remplacer

| Emplacement | Fichier de cette livraison |
|---|---|
| Serveur : dossier `mods/` | `haute-capitale-quetes-HF12-PQ02-HF9-PQ03-R1-HF1-1.21.11.jar` |
| Chaque client : dossier `mods/` | Le même JAR de quêtes |
| Serveur : dossier `datapacks/` du monde actif | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8_PQ03_R1_HF1.zip` |
| Serveur : même dossier `datapacks/` | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1_PQ03_R1.zip` |

Un seul exemplaire du mod `haute_capitale_quetes`, un seul core et un seul `hc_quetes` doivent rester actifs. Le nouveau JAR regroupe les fonctionnalités antérieures et PQ03. Les deux JAR serveur/client du paquet complet sont identiques.

Les deux ZIP de datapacks sont directement chargeables : chacun contient `pack.mcmeta` et `data/` à sa racine. Placer ces deux ZIP dans le monde ; ne pas placer l’archive générale d’installation dans `datapacks/`. Si l’installation utilise déjà des dossiers décompressés, remplacer intégralement les deux dossiers par le contenu des ZIP, au lieu de conserver en parallèle dossiers et ZIP.

## Étapes

1. Fermer les clients et arrêter proprement le serveur. Sauvegarder le monde entier, les profils Nexus et les trois fichiers précédents. Conserver cette sauvegarde avant qu’un personnage ne commence PQ03 R1.
2. Remplacer les trois composants du tableau. Conserver les autres mods et datapacks installés : **Nexus OPT3, HUD OPT1, Dialogue b10 et EasyNPC ne sont pas remplacés**.
3. Redémarrer complètement le serveur et les clients. Un simple `/reload` ne charge pas les nouvelles classes ou les mixins du JAR.
4. Près du parcours existant d’Aurèle, exécuter une fois :

   ```mcfunction
   /function capitale:pq03/admin/preparer_parcours
   /function capitale:pq03/admin/diagnostic_points
   ```

   La préparation mémorise les vrais markers chargés A–V, sans inventer de coordonnées ni déplacer les points. Si certaines parties ne sont pas chargées, parcourir ces zones et relancer la préparation : elle conserve les points déjà mémorisés. Un doublon ou un point requis introuvable doit être corrigé avant l’escorte. Les armor stands de visualisation ne sont pas utilisés comme waypoints.

5. Le PNJ déjà tagué `npc_aurele_veyrane` est reconnu automatiquement. Si le diagnostic signale une source non reconnue, s’approcher du seul Aurèle public, à moins de quatre blocs, hors scène PQ03, puis exécuter :

   ```mcfunction
   /function capitale:pq03/liaison/aurele
   ```

   Cette liaison mémorise son apparence et son identité ; elle ne crée pas de PNJ public supplémentaire. Les actions publiques existantes conservent leurs identifiants.
6. Tester la rencontre après PQ02, choisir une route et utiliser le diagnostic si nécessaire :

   ```mcfunction
   /function capitale:pq03/admin/diagnostic
   ```

Les commandes de placement existantes des 22 waypoints restent valables. Il n’y a ni remise à zéro des personnages, ni nouvelle pose de Léovic/Gautier/Perrin, ni commande PQ01/PQ02 à refaire.

## Grondement au point D

Le bloc B02 ne doit être prononcé que si le grondement est réellement audible au point D. Il est **désactivé par défaut**. Après vérification à l’oreille sur la carte :

```mcfunction
/function capitale:pq03/admin/confirmer_grondement
```

Pour le désactiver :

```mcfunction
/function capitale:pq03/admin/desactiver_grondement
```

Aucun son ambiant artificiel n’est ajouté. Les autres pauses de lore et la quête fonctionnent avec B02 désactivé.

## Personnages déjà en cours de quête

Les états canoniques hérités sont conservés. Un personnage terminé reste terminé et ne reçoit aucun gain rétroactif. Un personnage déjà en escorte est repris dans sa branche, en pause ; il peut relancer la conversation pour reprendre. L’ancien guide partagé ne permettait pas de connaître une position privée exacte : la migration utilise le repère de branche cohérent à proximité du personnage, sinon le début de sa branche. Un ancien état de retour est repris depuis I. Après initialisation R1, la position, le segment, la page et les décisions sont persistés par personnage.

Le guide historique invisible est stationné sur A par la préparation uniquement quand A est chargé, qu’un seul ancien guide est trouvé et qu’aucune scène PQ03 n’est active. Aucun acteur privé d’un autre joueur n’est supprimé par cette opération.

## Recette sur la carte

- Route directe : A → J → … → V, avec arrêts de lore N et S.
- Route commerces : A → … → I ; Aurèle revient I → … → A pendant la visite libre ; retrouvailles à A puis route vers V. Arrêt G, et D si le grondement est confirmé.
- En déplacement, ouvrir les questions : Aurèle doit s’arrêter, puis reprendre le même segment à la fermeture ou par la réponse de reprise.
- Quitter V sans conclure : un rappel privé unique, aucune caméra forcée ; revenir poser les questions finales. La réputation et la fin de quête doivent attendre la fermeture de l’au revoir.
- Deux joueurs : chacun voit son escorte et conserve sa route, ses questions, ses récompenses et son marqueur. Vérifier avec deux vrais clients les angles, la vitesse sur les obstacles réels et les FPS/TPS.

## Retour à la version précédente

Arrêter le serveur et les clients. Restaurer ensemble les trois composants antérieurs **et la sauvegarde du monde et des profils prise avant migration**, puis redémarrer. Revenir seulement au vieux pilote d’escorte après avoir joué R1 n’est pas une migration descendante garantie : les anciennes fonctions ne comprennent pas les positions et pages privées ajoutées par R1.

Voir `COMMANDES_PQ03.md`, `RAPPORT_PQ03_R1_HF1.md` et `SHA256SUMS.txt` pour les commandes exactes, le périmètre des changements et les limites des tests.
