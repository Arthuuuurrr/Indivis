# Changements et compatibilité

## Correctif SI1a

Lecture NBT non destructive, copie native synchrone et modèle du garde réutilisable. Rayon de capture porté à huit blocs, sélection parmi les gardes admissibles, diagnostic explicite. Seuls le journal et le pack de quêtes changent depuis SI1. Voir `CORRECTIF_SI1a.md`.

## Core et quêtes

Base : core 1.5.7, pack de quêtes b21 LIVRES_LORE_v16_1, journal b6 et Nexus PRE34 fournis.
Les définitions manquantes référencées et les entrées de gardes nécessaires sont reprises dans l'ancienne référence
1.5.6 RC9h. L'ajout d'une définition ne l'active pas comme nouveau tick global.
Les quatre fichiers optimisés de tick des gardes sont conservés octet pour octet.
Le tick central conserve ses contrôles existants et reçoit une pause limitée au dialogue d'accès des Archives.

Les 66 anciennes fiches sont normalisées en identifiants canoniques et états dédiés. Les anciens scores partagés
ne sont plus maintenus comme curseurs de jeu : lectures converties en prédicats, écritures en vecteurs d'états.
Un import unique, verrouillé par `hca_v2_schema`, reprend les valeurs historiques du personnage actif.
Les entrées de PNJ historiques, API transverses, marqueurs hérités et compteurs externes restent compatibles.
Les identifiants `hc_quete` des objets sont remappés ; un convertisseur conserve les autres composants
des anciens objets dans les 36 cases d'inventaire et la main secondaire, y compris lors d'une reprise ultérieure.
Les arguments de macro `$(id)` restent génériques. Les 33 contenus des livres de quêtes sont identiques aux originaux.

Les autorisations des Archives sont composées par source : individuel, judiciaire, rang, ancien droit temporaire,
MQ02 et MQ03. La sortie retire seulement la source MQ correspondante. La règle du rang et les autres zones restent en place.
Le garde cloné reçoit le routage MQ et garde ses chemins de contrôle du Cœur raccordés aux Archives.

## Journal b7 SI1 — trois classes d'origine modifiées

- Initialisation : branche l'adaptateur de Séquence I.
- QuestVisibility : lit les anciens tags de visibilité depuis les états canoniques ; filtre les entités privées
  par compte et personnage actif, sans remplacer la personnalisation 3D.
- TargetResolver : lit la dimension enregistrée pour une cible `zone:` au lieu de forcer l'Overworld.

Les nouvelles classes d'adaptation utilisent les API constatées dans les JAR fournis. Elles invalident les caches du
journal au changement de personnage, remappent les anciens IDs dans l'attachement existant et raccordent le suivi MQ,
les positions, le total de points et les sessions de dialogue. Les flux réseau conservent l'UUID Minecraft normal.
Le schéma JSON du journal n'est pas remplacé et aucun stockage de progression concurrent n'est ajouté.

## Nexus PRE34 SI1 — deux classes d'origine modifiées

Les politiques de sauvegarde/vidage reconnaissent `hch_*`, `hcf_*`, `hcd_*`, `hca_*` comme données par personnage,
et `hcm_*` / `hcq_mqscene` comme états transitoires. Les familles existantes `hcq_*`, `CAP_*` et `ACCESS_*`
conservent leur mécanisme de profils. La nouvelle classe V2QuestPolicy centralise ces préfixes.
Les classes originales extérieures à ces cinq points de modification, dont client et cosmétiques, sont inchangées.

## Limites de compatibilité à vérifier en jeu

Les points d'API réfléchis correspondent aux versions fournies et à Minecraft 1.21.11 ; une autre version de mods
demande une nouvelle vérification. Le vérificateur de bytecode et les doubles de banc ne prouvent pas la résolution
effective de toutes les API au lancement Fabric. Le NBT EasyNPC, le clonage réel, les permissions Brigadier,
les interactions Xaero et les performances nécessitent la recette serveur décrite séparément.

Le point d'entrée Josué, absent des fichiers fournis, est déclaré comme tag de fonction optionnel pour accueillir
son éventuel pack complémentaire. Aucun dialogue Josué n'est inventé. Les biomes du core ne sont pas modifiés ;
le messager utilise un tag distinct de localités impériales, vérifié dans le lore, excluant les cités étrangères.
Le seuil, la géométrie des Archives et le placement des PNJ sont décrits dans le guide.
