# ADDENDUM WORK — PRÉSERVER LE RETOUR À ALTHÉON BRUMEFORGE DANS MQ03

**Projet :** Indivis, Minecraft Fabric 1.21.11.  **Contexte :** rework PQ02 v1.0 avec un unique correctif autorisé de Hugues (PQ01).  **Statut :** exigence obligatoire de non-régression de la séquence I de la quête principale ; **pas** un mandat pour réécrire MQ01–MQ03.

## 1. Parcours narratif déjà fixé

- **MQ01 — « La convocation » :** activation après la fin du prologue, lorsque le personnage atteint au minimum le rang **Résident** et a gagné au moins **5 `CAP_POINTS_TOTAL`** (seuil provisoire). **La citoyenneté n'est pas requise.** Le messager remet la convocation des Archives.
- **MQ02 — « Les registres » :** rencontre avec le Scribe, consultation du dossier et de l'Archivaire. L'Archivaire explique que le rapport de récupération signé par Althéon ne contient pas toutes les observations de navigation. **À la fin de MQ02 :** nouvelle quête/étape « Retrouver le capitaine Althéon Brumeforge au Port de la Haute Capitale » (MQ03).
- **MQ03 — « Le journal de bord » :** le joueur retrouve **le même PNJ Althéon Brumeforge** que dans PQ01. Dialogue spécifique de MQ03 et consultation de son journal de bord (exemplaire de travail). La position de l'île du sauvetage reste incertaine ; **le monolithe et l'île précise ne doivent pas être révélés** par le capitaine. Suite : objectif « Retourner auprès de l'Archivaire aux Archives de la Couronne » ; une **nouvelle autorisation de passage** permet de franchir les Sentinelles. L'Archivaire confronte les renseignements aux cartes et dirige le joueur vers **Havre-Fort**, ouvrant **MQ04**.

Cette chronologie ne doit pas être modifiée par le rework PQ02 : la mention initiale du rapport dans PQ02 n'équivaut PAS à la consultation ultérieure du journal dans MQ03. La fin de PQ02 ne complète pas MQ03 et ne supprime aucun dialogue de Brumeforge.

## 2. Trace technique constatée dans l'ancien core HF7 — NON AUTORITÉ DE PRODUCTION

Archive historique inspectée : `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip`. Exemples de chemin réellement lus :

- `data/capitale/function/npc/altheon_brumeforge/interact_self.mcfunction` appelle `capitale:quest/spawn_dirigeable_couronne/capitaine_large/interact_self` ; d'autres alias `npc/capitaine_altheon` / `npc/capitaine_altheon_brumeforge` redirigent vers ce routeur.
- `data/capitale/function/quest/spawn_dirigeable_couronne/capitaine_large/interact_self.mcfunction` appelle `capitale:pq01/open/altheon_self`.
- `data/capitale/function/pq01/open/altheon_self.mcfunction` donne **priorité à MQ03**, en commençant par :

```mcfunction
execute if score @s hcq_mq03 matches 10..30 run return run function hc_quetes:runtime/interact/altheon_brumeforge
```

Ce résultat prouve **l'existence historique d'un raccord** dans HF7, mais ne prouve pas son bon fonctionnement en jeu et ne garantit ni les mêmes fichiers ni les mêmes seuils dans le core réellement installé. Work doit réinspecter les versions de production avant tout changement. Attention à ne pas remplacer un routeur d'Althéon existant par une simple interaction PQ01 ou par un texte générique de clôture.

## 3. Règles impératives pour l'intégration

1. Réutiliser l'entité Althéon réellement présente, avec son apparence, sa position, son EasyNPC, ses voix, ses caméras et ses fonctions de liaison actuelles. Ne pas le faire disparaître définitivement à la fin de PQ01 ; éviter les doubles PNJ permanents.
2. Préserver **tous les états et déclenchements** de MQ01–MQ03 : `hcq_mq01`, `hcq_mq02`, `hcq_mq03`, `CAP_POINTS_TOTAL`, condition Résident, autorisations MQ02/MQ03 des Sentinelles, jalons du journal, marqueurs `!/?` et waypoints Xaero de la quête suivie.
3. Router l'interaction d'Althéon selon **l'étape de quête du personnage Nexus actif** : PQ01 tant que pertinente, MQ03 lorsque l'enquête du journal est active, puis dialogue de repli valide après l'accomplissement. Ne pas utiliser uniquement l'UUID du compte comme clé durable.
4. La refonte de PQ02 et la suppression du choix 6 de Hugues ne doivent pas supprimer/renommer les fonctions servant de pont entre `capitale` et `hc_quetes`, ni modifier les stades MQ pour masquer un conflit.
5. **Aucune refonte de MQ01–MQ03** dans cette mission. Tout micro-correctif de raccord indispensable doit être isolé, audité et annoncé explicitement ; ne pas déplacer le capitaine, recalculer les seuils ou réécrire ses dialogues.

## 4. Matrice de tests avant livraison

| Test | Attendu |
|---|---|
| PQ01 active | Althéon répond toujours avec les dialogues du prologue ; Hugues perd uniquement son choix n° 6 |
| PQ01 / PQ02 achevées, MQ03 inactive | Althéon n'est pas supprimé et ne lance pas prématurément son journal |
| MQ02 terminée, MQ03 à l'étape Althéon | Le dialogue MQ03 s'ouvre avec ses questions et transitions, sans retour au menu PQ01 |
| Consultation du journal terminée | Objectif de retour vers l'Archivaire présent et sauvegardé |
| Retour aux Archives | Autorisation MQ03 correctement transmise aux Sentinelles, sans supprimer d'autres droits |
| Dernier entretien avec l'Archivaire | MQ03 terminée, MQ04 ouverte et waypoint Havre-Fort fonctionnel si module présent |
| Deux personnages Nexus d'un même compte | Progressions PQ/MQ et dialogues distincts ; A peut être en MQ03 sans forcer B |
| Déconnexion, changement de personnage, interactions concurrentes | Aucun blocage de dialogue, duplication de PNJ, perte d'accès ou réinitialisation de la progression |

**Livrable de contrôle :** un tableau des chemins réellement en service et leur état avant/après ; préciser quels tests sont statiques et lesquels ont véritablement été effectués dans Minecraft.

## 5. Sources à donner en lecture seule

`Indivis_Prompt_Work_Sequence_I_MQ01_MQ03_v1_1_PLACEMENT_GARDES.md` contient la scénarisation détaillée, l'ordre des scènes MQ01→MQ04, le dialogue d'Althéon et les autorisations. Le fichier est un **référentiel pour préserver l'existant**, pas une demande de reconstruire ces quêtes.

**Le ZIP du core ACTUEL du serveur reste indispensable** pour conclure sur l'implémentation. Ne pas livrer de correction basée sur HF7 sans comparer au core installé.
