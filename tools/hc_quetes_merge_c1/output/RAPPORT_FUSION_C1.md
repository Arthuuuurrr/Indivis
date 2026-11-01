# Fusion hc_quetes C1 — 1er novembre 2026

## Résultat

Les corrections b22 de Clément sont fusionnées dans la version d’Arthur **V2 / SI1b / PQ01 / QMARK2 / PQ02 HF8**. Le datapack livré contient **3 330 fichiers** : **17 fichiers existants modifiés**, **7 ajouts** et **3306 fichiers conservés octet pour octet**. Aucun fichier existant n’est supprimé.

Le ZIP est directement installable : `pack.mcmeta` et `data/` sont à la racine. Le core et les JAR HF8 restent inchangés.

## Méthode et provenance

La comparaison directe des deux fichiers fournis mélangeait les corrections de Clément avec la migration v2. La base commune **b21 originale**, antérieure aux adaptations de lore, a été retrouvée et identifiée par son empreinte. Clément y a changé **12 fichiers** et ajouté **2 fonctions**. Ses différences de mise en forme JSON n’ont pas été appliquées comme des modifications de jeu.

Le convertisseur historique et ses tables de migration gelées de SI1b sont réutilisés pour traduire les étapes, objectifs auxiliaires et objets. Les valeurs ne sont pas renumérotées à partir des nouveaux textes. Une fusion à trois versions conserve les modifications d’Arthur ; elle résout notamment le tick sans perdre ses branchements de séquence I. Aucun conflit n’est laissé dans la livraison.

| Entrée | SHA-256 |
| --- | --- |
| Clément `hc_quetes-0.1.0.b22.zip` | `07d0c6e3b711fa9ccf1808e568ce72cc4cb0b577132013f40513aa6b066df6a3` |
| Arthur `hc_quetes…PQ02_R1_HF8.zip` | `393ac02d857774f4de7483ce4d179f2e67a13f1ec1ae9cab4830608568b891a6` |
| Base commune `hc_quetes-0.1.0.b21.zip` | `4e14c6584a175c864bccf3dbf36569c601cab013078dbc9ce0335f47a8a17360` |
| Fusion C1 livrée | `d9a0fb0d0aecd0eb4f9bc64f9cb7bae932ddaaa7f67273b042512c22ff9e67aa` |

## Corrections de Clément intégrées

| Sujet | Comportement conservé dans la fusion |
| --- | --- |
| Insigne d’Orrin Vasca | Une mort ultérieure peut rendre l’insigne perdu, sans redonner la boussole. L’insigne n’est pas régénéré après la quête qui en a besoin. |
| Mère Aveline | L’interaction peut rendre l’insigne manquant puis enchaîner le pardon dans le même appel. |
| Remise à Hector | L’étape n’avance qu’après réception effective de l’insigne ; un inventaire plein produit le message prévu. La reprise d’un dossier déjà bloqué peut rendre l’insigne manquant. |
| Caisse de médicaments | Le remplacement exige réellement dix Martins et en retire exactement dix. Les montants 0, 1, 3, 9, 10, 11 et 20 ont été exercés. Le message pour un montant insuffisant explique l’inventaire principal et le sac à dos ; le datapack ne prélève pas à l’intérieur d’un sac de mod. |
| Offre d’Aurèle refusée | Le « ! » disparaît après refus, mais l’offre reste accessible au clic et peut être acceptée plus tard. |
| Documents souvenirs | Retrait en fin de quête selon les bornes de Clément, avec conservation des documents encore utilisés par une quête suivante et respect des différentes branches. |
| Cages de Colin | L’objectif de clé est distingué de l’ouverture des cages ; guidage et descriptions fournis par Clément conservés avec les identifiants v2. |

Le refus d’Aurèle utilisait un nouveau `hcq_n=-1` dans b22. La fusion l’adapte avec **`hcf_cqvcbb01_ref`**, un drapeau de personnage distinct : les étapes canoniques restent à 0 avant acceptation. Le marqueur seul est filtré ; l’interaction manuelle reste ouverte. Le drapeau est créé automatiquement, conservé par Nexus, effacé à l’acceptation et par le reset existant. Il n’y a pas de migration vers un ancien score partagé.

## Préservation des modifications d’Arthur

- Les **70 identifiants canoniques** restent présents, sans doubles fiches sous leurs anciens noms.
- **69 définitions de quête** sont strictement identiques à HF8 ; seule la fiche des ruines de Carrec reçoit le changement de Clément concernant Colin.
- PQ02 garde son versement systématique de **50 Martins pendant F06**, ses réponses et ses gains conditionnels, sans retour requis ni +5 automatique.
- Les fonctions de la séquence I, liaisons de gardes, runtime MQ et données de marqueurs sont identiques à HF8. Le retour MQ03 vers Althéon puis les Archives est exercé dans la régression.
- Les objets et livres de lore d’Arthur, leurs textes et leur mise en page sont **identiques**. Les anciennes versions de livres présentes dans le b22 brut ne remplacent pas le travail de lore.
- Les JAR et le core ne sont pas recompilés ni remplacés pour C1. Les parcours et liaisons de PNJ déjà placés sont réutilisés.

## Tests exécutés

| Vérification | Résultat |
| --- | --- |
| Intégrité, conservation, références et correspondances d’états | **117 contrôles statiques PASS** |
| Banc serveur complet sur l’archive fusionnée | **1 580 assertions PASS**, dont **105 spécifiques à C1** |
| Décodage par le véritable journal HF8 | **70 fiches**, **0 erreur** |
| Chargement des fonctions de la fixture | **0 échec** après substitution explicite des dépendances tierces absentes |

Les contrôles C1 portent sur les paiements exacts, la récupération et la non-duplication des insignes, l’inventaire plein, le refus/reprise d’offre, la persistance du nouveau drapeau sur disque Nexus et l’isolation entre personnages. Le balayage est exécuté pour les documents et les fins de branches, avec un second joueur dont la quête est encore en cours. Le relevé du naturaliste et la copie réutilisée dans J09 restent disponibles tant que leur dernière quête dépendante n’est pas terminée.

La régression HF8 rejoue deux parcours de PQ02 jusqu’au magistrat puis à l’ascenseur, les choix et reprises de la scène finale, les sauvegardes Nexus, l’offre suivante d’Aurèle, Hugues et MQ03. Le vrai tick core tourne 773 fois, avec 245 intercalations narratives. Les résultats sont liés au JAR HF8 d’origine et au ZIP C1 par leurs empreintes.

## Limites

Serveur Fabric/Minecraft 1.21.11, véritables fonctions, entités EasyNPC, dialogues et moteur de journal ; pas de client graphique ni de carte Indivis chargée.

Connexions, sélection de personnage Nexus et disponibilité de caméra synthétiques. Écriture/chargement des profils OPT3 réels, dont le nouveau drapeau de refus et son isolation entre personnages.

Monnaie remplacée par des pépites d’or dans le banc ; CapSkills et bonus aléatoires comptés par fixtures. mmospawn est un service simulé et sept récompenses d’équipement inchangées de mods tiers sont des placeholders non exercés.

Aucun substitut de banc dans le datapack livré. Pas de mesure de FPS/MSPT, de rendu des livres, d’écoute des voix ni de prélèvement natif dans un mod de sac à dos.

Les erreurs intermédiaires de fixture (objectifs de test incomplets, monde réutilisé et dépendances externes non déclarées) ont été corrigées dans le banc. Elles ne sont pas des changements apportés au datapack livré. Les résultats ci-dessus sont ceux du dernier passage complet, pas de ces tentatives intermédiaires.

## Installation

1. Arrêter le serveur et conserver une copie du datapack remplacé.
2. Dans `<monde>/datapacks/`, remplacer uniquement l’ancien `hc_quetes` par **`hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1.zip`**. Conserver une seule version, y compris si l’ancienne était décompressée.
3. Garder le **core PQ02 HF8** et le **JAR de quêtes HF8** déjà installés, puis redémarrer le serveur.

**Aucune commande, pose, liaison de PNJ ou remise à zéro à refaire. Aucun fichier client à changer pour cette fusion.** Le drapeau ajouté se crée avec les fonctions de chargement normales du datapack.

## Ressources modifiées ou ajoutées

Les noms ci-dessous sont relatifs à `data/hc_quetes/`, sauf `pack.mcmeta` :

| Ressource |
| --- |
| `function/aurele/interact.mcfunction` |
| `function/aurele/q12/recaisse.mcfunction` |
| `function/aurele/q8/kill_vasca.mcfunction` |
| `function/carrec/aurele/refus.mcfunction` |
| `function/carrec/choix.mcfunction` |
| `function/hector/q5/prend_insigne.mcfunction` |
| `function/hector/q5/retour.mcfunction` |
| `function/lib/balayage.mcfunction` |
| `function/marqueurs/aurele.mcfunction` |
| `function/migration/ensure/arc_n.mcfunction` |
| `function/migration/load.mcfunction` |
| `function/migration/write/arc_n/v0.mcfunction` |
| `function/migration/write/arc_n/v1.mcfunction` |
| `function/pnj/aveline/interact.mcfunction` |
| `function/tick.mcfunction` |
| `hcq_quests/cq_vc_ep_02_ruines_carrec.json` |
| `pack.mcmeta` |
| `function/aurele/q12/recaisse_paye.mcfunction` |
| `function/aurele/q8/rend_insigne.mcfunction` |
| `predicate/migration/state/arc_a/6_to_.json` |
| `predicate/migration/state/arc_c/70_to_.json` |
| `predicate/migration/state/arc_n/70_to_.json` |
| `predicate/migration/state/arc_n/neg1_to_0.json` |
| `predicate/migration/state/arc_p/70_to_.json` |
