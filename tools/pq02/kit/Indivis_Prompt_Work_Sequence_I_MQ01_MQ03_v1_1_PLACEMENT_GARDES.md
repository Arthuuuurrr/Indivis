# INDIVIS — MANDAT WORK v1.1 : IMPLÉMENTATION COMPLÈTE DE LA SÉQUENCE I (MQ01–MQ03)

**Minecraft Java 1.21.11 / Fabric — Projet Indivis / Haute Capitale.**
**Finalité : produire les véritables quêtes installables, les PNJ EasyNPC générés individuellement à l’emplacement choisi par l’administrateur, les gardes de passage conservant toutes leurs fonctions, et les dialogues déjà validés, pas seulement une analyse ou un prototype.**

## 0. FICHIERS À FOURNIR À WORK ET ORDRE DE PRÉÉMINENCE

Fournir les **versions actuellement installées** du datapack `capitale_core`, du datapack `hc_quetes`, du JAR `haute-capitale-quetes`, du JAR EasyNPC, du JAR NexusCharacters, du JAR CapSkills, de la configuration pertinente, et si nécessaire des scripts ou exports de PNJ EasyNPC déjà créés par Clément. Fournir également `Indivis_Norme_Quetes_v2_Migration_Integrale.zip` et, pour la connaissance du lore, `Haute_Capitale_Dossier_Canonique_Actuel_v16_0(1).pdf`.

Les anciens exemples `capitale_core_1.5.6-RC9BC_INTERPELLATION_CONTINUITY_FULL(1).zip`, `hc_quetes-0.1.0.b21_LIVRES_LORE_RECANON_v16_1(2).zip` et `haute-capitale-quetes-0.1.0+1.21.11.b6(2).jar` sont **des références techniques historiques, pas nécessairement les versions à déployer**. Le core peut désormais être en 1.5.7 ou davantage, Nexus en PRE34 ou ultérieur. Ne jamais reconstruire une version finale à partir d'une archive plus ancienne si une version de production plus récente existe. Comparer les versions, fichiers et empreintes avant toute modification.

**La norme v2 est obligatoire**, elle remplace la v1 : les 66 quêtes historiques doivent, elles aussi, être normalisées, et les nouveaux travaux doivent s'appuyer sur la version issue de cette migration. Ne pas créer de second schéma concurrent. Si la migration v2 n'est pas encore effectuée, identifier cette dépendance bloquante ; préparer les nouvelles quêtes de façon isolée et compatible v2, puis demander la version réellement migrée avant d'intégrer ou de livrer des ZIP de production. Ne pas renommer isolément quelques fiches en prétendant avoir migré toutes les quêtes.

Avant la mise en œuvre, vérifier la réalité des commandes et NBT dans **le JAR EasyNPC effectivement installé** et comparer avec **une commande ou un export de PNJ complet produit par Clément**, si disponible. S'il manque des champs ou capacités, ne pas inventer leur syntaxe ; demander l'exemple ou signaler précisément ce qui n'est pas automatisable.

## 1. OBJECTIFS ET LIVRABLES

1. Créer une **implémentation jouable complète** des quêtes `mq_01_convocation`, `mq_02_registres` et `mq_03_journal_bord`, conformément à la norme v2 et aux dialogues reproduits ci-dessous. Préparer la transition vers `mq_04_havre_fort` **uniquement comme ouverture de quête et premier objectif**, sans développer l'enquête maritime ni l'île.
2. Fournir **une fonction de création/pose individuelle par PNJ EasyNPC** : l’administrateur se rend physiquement à l’emplacement voulu, puis exécute une seule commande de fonction pour créer CE PNJ complet à sa position et dans sa dimension (interaction, tags, fonctions, réglages vérifiés). **Ne pas imposer de commande globale de déploiement et ne pas inventer de coordonnées.** Réutiliser et enrichir les gardes et personnages déjà présents ; les skins restent gérés par Clément et ne doivent pas bloquer l’intégration. Prévoir le contrôle des doublons et les commandes de vérification.
3. Intégrer les dialogues complets en `.mcfunction` et un mécanisme de choix interactifs suivant le router existant, avec **questions facultatives répétables et un choix explicite pour poursuivre**.
4. Intégrer chaque quête au **journal de Clément**, aux marqueurs `!`/`?` au-dessus des PNJ et au **waypoint Xaero** de la quête suivie (Minimap installé, World Map également), en respectant le système réellement présent.
5. Garantir la progression **par personnage Nexus**, y compris pour deux personnages sous un même compte Minecraft ; les PNJ scénarisés privés, marqueurs, waypoints, dialogue, courrier et accès doivent viser le personnage ACTIF, pas seulement l'UUID du compte.
6. Livrer deux datapacks modifiés cohérents et éventuellement des JARs seulement si absolument nécessaire, avec scripts de déploiement, documentation des tests, diff, rapport de compatibilité, SHA-256, rollback. Ne pas modifier automatiquement le serveur de production.

## 2. NOMENCLATURE OBLIGATOIRE (NORME V2)

Le registre `REGISTRE_CANONIQUE_70_IDS.csv` fixe :

| Quête | ID de fiche JSON | code court | scoreboard de progression | type | arc |
|---|---|---|---|---|---|
| MQ01 | `mq_01_convocation` | `mq01` | `hcq_mq01` | `MAIN` | `arc_global_mq` |
| MQ02 | `mq_02_registres` | `mq02` | `hcq_mq02` | `MAIN` | `arc_global_mq` |
| MQ03 | `mq_03_journal_bord` | `mq03` | `hcq_mq03` | `MAIN` | `arc_global_mq` |
| MQ04 (transition seulement) | `mq_04_havre_fort` | `mq04` | `hcq_mq04` | `MAIN` | `arc_global_mq` |

Fiches : `data/hc_quetes/hcq_quests/<quest_id>.json`. Fonctions à organiser par exemple sous `data/hc_quetes/function/quests/mq01/...`, `.../mq02/...`, `.../mq03/...`; adapter les noms des ponts vers `capitale` à l'architecture réellement migrée.

- Score principal dédié `hcq_<code>`, avec état `0` = inactive, `10` = commencée, `20/30/40...` = étapes, `100` = terminée ; les limites exactes et correspondances `source.active`, `turn_in`, `completed`, `objectives[].done` doivent être documentées et compatibles avec le parseur du JAR réel.
- Compteurs éventuels `hcq_<code>_oN`, choix persistants `hch_<code>`, flags `hcf_<code>`, **sans collision et sans dépasser les contraintes Minecraft sur la longueur des objectifs**.
- Noms de PNJ `hcq_pnj_<slug>`, de marqueurs `hcq_mrk_<slug>`, de fonctions préfixés de la bonne namespace. Identifiants numériques d'activation des PNJ, déclencheurs et choix **centralisés dans un registre** ; vérifier toutes les plages occupées par le routeur actuel. Aucune allocation au hasard.
- Objet convocation : identifiant stable, par exemple `hcq_item_mq01_convocation`, à consigner dans un registre et dans `minecraft:custom_data` selon le protocole du pack ; ne pas changer les identifiants hors des quêtes.
- `CAP_POINTS_TOTAL` reste la donnée de progression globale cumulée, **pas un nouveau type de points** ; le compteur ne diminue pas avec les dépenses. Le passage Résident et la fin de prologue sont vérifiés via les états actuels du core.
- Les 33 livres de Clément et les quêtes historiques déjà migrées restent inchangés, sauf adaptation strictement nécessaire et justifiée.

## 3. PNJ EASY NPC — UNE COMMANDE PAR PNJ, À L’EMPLACEMENT CHOISI

### 3.1 Constat vérifié et mode opératoire

Le datapack historique de Clément démontre une création **d’un PNJ individuel** à la position de l’exécutant :

```mcfunction
summon easy_npc:humanoid ~ ~ ~ {CustomName:"Hector Valmont",EasyNPCVersion:3,Tags:["indivis_no_hard_despawn","hcq_pnj","hcq_pnj_hector","hc_dialogue"],ActionData:{ActionPermissionLevel:2,ActionEventSet:{ON_INTERACTION:[{Type:"COMMAND",Cmd:"trigger hcq_pnj set 1",ExecAsUser:1b,PermLevel:2}]}}}
```

Il s'agit d'un **exemple historique**, pas d'un modèle à recopier aveuglément : vérifier NBT et identifiants dans la version EasyNPC réellement installée. **L’administrateur choisit lui-même chaque emplacement en jeu.** Pour un PNJ inédit, fournir une fonction de pose (`hc_quetes:pose/pnj/<identifiant>` ou le chemin réellement normalisé), invoquée depuis la position souhaitée, qui configure le PNJ intégralement en **une commande par PNJ**. Gérer sa direction/orientation si disponible ; sinon documenter l'ajustement nécessaire. Le chemin de fonction montré ici est un exemple à vérifier avec la norme v2.

**Il n'est pas demandé de générer une commande unique plaçant tous les PNJ du monde.** Cette commande supposerait des coordonnées, des dimensions et des orientations préalablement relevées. Elle serait inappropriée aujourd'hui. Si, plus tard, ces informations sont validées, un installateur global pourra être créé séparément, sans devenir une dépendance de MQ01–MQ03.

**Skins, tenues et apparences :** Clément les intègre lui-même. Préserver cette méthode ; ne pas générer ni attribuer arbitrairement de skins et ne pas écraser ceux des PNJ existants. Les fonctions de dialogue et les paramètres vérifiés peuvent être créés en une commande ; le travail manuel de cosmétique demeure possible.

### 3.2 Différencier nouveaux PNJ et PNJ déjà installés

- **Messager de la Couronne (nouveau, MQ01)** : PNJ temporaire visible uniquement pour le personnage Nexus actif concerné, apparaissant dans les zones habitées impériales admissibles. Déploiement **dynamique par la quête**, sans pose manuelle permanente. Identifier les limites de visibilité d'EasyNPC et traiter le changement de personnage Nexus.
- **Sentinelles des Archives (gardes de passage)** : **ne pas les remplacer par de simples `easy_npc:humanoid` dialoguant**. Elles doivent être créées **sur le même modèle fonctionnel que les gardes qui protègent actuellement les accès au Cœur**, ou enrichies sur place si déjà présentes. Voir §3.3.
- **Scribe des Archives (nouveau ou à réutiliser)** : un PNJ permanent dans la grande salle sous le dôme ; pose manuelle sur place, fonction de création complète.
- **Archivaire de la Couronne (nouveau ou à réutiliser)** : un PNJ permanent à une table de lecture de cette même salle ; pose manuelle sur place, fonction de création complète. Aucun nouveau bureau ou étage n'est supposé.
- **Althéon Brumeforge (existant)** : **réutiliser l'entité et son apparence existantes**. Ajouter seulement les nouveaux dialogues de MQ03 par condition d'étape, sans créer de double ni altérer les fonctions du prologue.

Pour tout PNJ déjà présent, fournir plutôt une **fonction de liaison / mise à jour sûre** ciblant un tag ou identifiant stable, et sauvegarder ses données auparavant. Une fonction de pose ne doit jamais engendrer de double lorsqu'un garde existe déjà. Ne pas chercher une entité par son seul nom visible si plusieurs copies peuvent exister.

### 3.3 EXIGENCE NON NÉGOCIABLE : Sentinelles des Archives = gardes de contrôle des accès

Les Sentinelles qui gardent les Archives ne sont **pas** de nouveaux PNJ décoratifs. Reprendre le **modèle fonctionnel des gardes du Cœur surveillant les accès** dans le datapack et dans les entités réelles du monde. Cette réutilisation porte sur la sécurité et le comportement, pas seulement sur l'apparence ou une ligne de dialogue.

**Audit préalable obligatoire :** retrouver les gardes de référence réellement placés, leurs tags/NBT EasyNPC, actions `ON_INTERACTION`, gestion de proximité et d'agression, détecteurs de franchissement, scoreboards, conditions d'accès, sélection de joueur et timers. Examiner le code core en production et, si le comportement réside dans les NBT d'une entité du monde, demander **l'export d'un garde opérationnel**. L'archive de référence RC9BC contient, entre autres, des briques sous :

- `data/capitale/function/npc/gardeprofondeurs/static_port/` : appels de proximité, interaction, classification, priorité à la criminalité, variantes par rang/réputation ;
- `data/capitale/function/npc/static_guards/defend_reset/` : gestion spécifique des gardes statiques et de leur retour au poste ;
- `data/capitale/function/npc/gardes/` : dispatch/tick de patrouilles ;
- `data/capitale/function/access/` et `data/capitale/function/core/tick.mcfunction` : autorisations et différents laissez-passer.

**Ces chemins attestent des mécanismes existants, mais ne prouvent pas que le garde précis du passage du Cœur est entièrement défini par ces fichiers** : vérifier également les NPC placés/configurés en jeu et l'éventuel système de contrôle externe. Ne pas copier une fonction de Garde des Profondeurs comme s'il s'agissait nécessairement du garde de passage demandé, sans vérification.

Fonctions à préserver et tester avant/après, pour chaque Sentinelle concernée :

1. Contrôle d'accès physique à la zone et refus normal aux joueurs non autorisés.
2. Interpellations de proximité, contrôle manuel, avertissements et reconduite vers l'extérieur ; maintien des choix existants tels que « Suivre le garde » / « Tenter un arrangement » lorsqu'ils sont applicables.
3. Gestion des délais d'avertissement, de dialogue et de reconduite, négociations et laissez-passer chronométrés du système existant.
4. Criminalité active et éventuelles agressions, réactions de la Garde, sanctions et protection des NPC.
5. Réputation/rang/race et autres branches de dialogue déjà prévues, sans contourner la priorité judiciaire.
6. Fonctionnement multijoueur, à l'échelle d'un **personnage Nexus**, sans bloquer, expulser ou autoriser par erreur un autre joueur.

**Greffer les conversations validées MQ01–MQ03 sur ce contrôle existant.** Ajouter un routeur d'interaction ciblant l'étape de quête du personnage, mais conserver un chemin de repli vers le comportement originel du garde hors contexte de quête. Pendant une véritable conversation autorisée, suspendre uniquement les timers concernés afin que le garde n'expulse pas le joueur au milieu de questions facultatives ; à la fermeture, reprendre le contrôle d'origine si l'accès n'est pas accordé, ou annuler la reconduite si l'accès MQ02/MQ03 est valide. Ne jamais désactiver globalement la sécurité de tous les gardes pour satisfaire un seul dialogue.

**Critère bloquant de livraison :** toute perte des fonctions de contrôle ou déplacement d'un joueur autorisé par un ancien timer impose correction ou refus de validation ; ne pas livrer les Sentinelles comme de simples NPC de quête si les contrôles du Cœur n'ont pas été préservés.

### 3.4 Fonctions de pose, vérification et retour arrière

Fournir :
- une commande de pose **individuelle** par nouveau PNJ permanent, exécutée par l'administrateur au bon endroit ;
- une commande de **liaison sûre** pour chaque PNJ existant, sans remplacement de ses données cosmétiques ni de ses comportements ;
- une commande de diagnostic/inspection (tags, type, fonction d'interaction, état du garde, emplacement et dimension), si techniquement faisable ;
- une sauvegarde/export et une restauration des PNJ existants modifiés ;
- une procédure séparée pour l'apparition/disparition dynamique du messager, sans spawn de doublon ;
- un registre indiquant quels PNJ sont à placer manuellement, déjà présents ou invoqués par événement.

Protéger les entités existantes de suppressions massives. Le rollback doit retirer les *nouvelles* entités sans toucher aux gardes/profils existants. La commande de pose doit fonctionner après suppression de son PNJ propre, tout en évitant les doublons par identifiant stable et dimension.

**Performances :** aucun spawn supplémentaire à chaque interaction/tick ; PNJ permanents partagés ; Scribe immobile lors de conversations individuelles. Les presets 3D Nexus sur PNJ sont un projet distinct et ne doivent pas être introduits sans audit de FPS.

## 4. DÉCLENCHEMENT MQ01

Conditions simultanées :
- Prologue achevé et **statut Résident au minimum**. **NE PAS EXIGER LA CITOYENNETÉ.** La citoyenneté impériale demeure facultative pour la quête principale, bien qu'elle conditionne d'autres branches.
- `CAP_POINTS_TOTAL >= 5` (seuil **provisoire**, facilement configurable). Total de points de compétences GAGNÉS depuis la création du personnage Nexus, sans décrément à la dépense, sans double attribution ni reset lors d'une reconnexion.
- MQ01 non déjà commencée ou terminée, messager non déjà remis, état idempotent.

Le messager doit apparaître **lors du passage en zone habitée impériale admissible**, hors combat, danger immédiat et situation invalidante. Réutiliser la cartographie des biomes de villes/safezones **mais ne pas considérer toutes les valeurs de `indivis:city` comme impériales** : Avaleiv, Skarnfjord, Urzak-Tor etc. peuvent être étrangères. Définir un tag/registre distinct des localités impériales réellement éligibles ; demander validation des zones ambiguës. Apparition naturelle et sûre à proximité du joueur, pas dans un mur, au-dessus du vide ou d'un lieu privé. Prévoir déconnexion, disparition et rechargement.

## 5. DIALOGUES — RÈGLES COMMUNES

Les libellés ci-dessous sont les textes **validés en principe**. Respecter le sens, la chronologie, les voix et la liberté de rôle du joueur. Une adaptation de ponctuation ou de longueur technique doit être documentée ; ne jamais modifier substantiellement la formulation sans validation. Tous les choix interrogatifs restent **répétables**, même déjà sélectionnés. Le choix marqué **[SUITE]** progresse l'état. Aucun clic de question répétable ne doit distribuer plusieurs fois courrier, récompense ou accès. Les dialogues interrompus et repris doivent rester cohérents. Utiliser le nom dynamique du personnage Nexus lorsqu'il existe, sans l'assimiler au nom de compte ni inventer une identité antérieure.

La race et la réputation ne définissent jamais le métier, la bravoure, le savoir, l'âge ou les actes du personnage. Les gardes peuvent avoir des **préjugés explicites** au nom d'une civilisation impériale à dominante humaine, sans les transformer en faits sur le protagoniste. Criminalité active prioritaire, puis réputation de la Garde du Cœur, puis race, puis dialogue contextuel. La réputation positive peut s'exprimer différemment selon la race ; elle **prime** sur le préjugé sans nécessairement le faire disparaître. Pas de nouveaux scores de « réputation de combat ».

## 6. MQ01 — LA CONVOCATION : SCRIPT

### 6.1 Messager, première interpellation

**Messager :** « Un instant, je vous prie. J'ai un pli des Archives de la Couronne à remettre en main propre à [Nom du personnage]. C'est bien vous ? »

- Choix 1 : « Oui, c'est moi. » → « Parfait. On m'a chargé de vous remettre cette convocation. Elle concerne votre dossier d'enregistrement auprès de la Couronne. » [SUITE]
- Choix 2 : « Les Archives ? Que me veulent-elles ? » → « Je l'ignore. Mon ordre porte uniquement sur la remise de ce courrier. Il est question d'une vérification de votre dossier, rien de plus. » [SUITE]

### 6.2 Instructions du messager

**Messager :** « Vous êtes prié de vous présenter aux Archives, dans la Haute Capitale. Les Sentinelles du Cœur qui en gardent l'entrée ont été informées de votre venue. Présentez-leur ce pli, elles vous laisseront passer. »

- Choix 1 : « Est-ce une convocation obligatoire ? » → « C'est une demande officielle des Archives. Je vous conseille de ne pas l'ignorer, mais aucune heure ne m'a été communiquée. » [RETOUR]
- Choix 2 : « Pourquoi ne m'ont-ils pas écrit directement ? » → « Certains courriers doivent être remis en main propre, particulièrement lorsqu'ils concernent les registres de la Couronne. C'est la procédure. » [RETOUR]
- Choix 3 : « Très bien, je m'y rendrai. » → « Dans ce cas, je vous laisse prendre connaissance de la lettre. Bonne continuation. » [SUITE]

Remettre le document **une seule fois**, à la conclusion du dialogue. Un objet de type livre/lettre avec **icône personnalisée de feuillet** via les ressources réellement disponibles ; ne pas créer un modèle qui ne sera pas visible sans resource pack correspondant. Texte du pli :

> À l'attention de [Nom du personnage],
>
> À la suite d'une vérification de votre dossier d'enregistrement, votre présence est requise auprès du service des registres des Archives de la Couronne, dans la Haute Capitale.
>
> Nous vous prions de vous présenter à l'entrée des Archives, muni du présent courrier.
>
> Les Sentinelles du Cœur ont reçu instruction de vous autoriser à accéder aux services concernés par cette convocation.
>
> Par ordre du service des registres des Archives de la Couronne.

La lettre est narrative ; l'accès dépend **aussi de l'état de quête**, pour ne pas bloquer le joueur qui la perd ou la range. MQ01 commence à la remise de la lettre (ou au terme du dialogue, une seule fois), objectif : « Présenter la convocation aux Sentinelles des Archives ». MQ01 **ne se termine pas** à cette remise : elle se termine à la validation de la convocation par le garde. MQ02 prend immédiatement le relais.

## 7. SENTINELLES — SCRIPT COMPLET ET CONDITIONNEL (FIN MQ01 / DÉBUT MQ02)

**Version de travail validée** : une première interpellation selon la *race + réputation*, sauf criminalité active où l'intervention judiciaire existante prime. Le joueur peut accéder avec la lettre ou son enregistrement de convocation. Les Sentinelles n'imposent pas la citoyenneté.

### 7.1 Accueil neutre (race)

- **Humain** : « Halte. Vous êtes ici dans l'un des établissements les mieux protégés de la Couronne. Si vous venez consulter les registres, je vais avoir besoin de votre autorisation. » (ne pas appeler automatiquement le Résident « citoyen »).
- **Nordique** : « Un Nordique ? Voilà qui change des érudits que nous recevons habituellement. J'espère que vous êtes ici pour consulter nos archives et non pour y chercher quelque chose à emporter. Votre autorisation, je vous prie. »
- **Nain** : « Un Nain, tiens donc. Plusieurs de vos compatriotes fréquentent nos collections techniques. Vous venez pour les mêmes raisons, ou pour tout autre chose ? Dans tous les cas, il vous faudra une autorisation. »
- **Elfe des Bois** : « Un Elfe des Bois, ici ? Je vous aurais plutôt imaginé sous les arbres que parmi nos vieux registres. Enfin, les Archives de la Couronne accueillent tous ceux qui y sont autorisés. Vous avez une convocation ? »
- **Haut-Elfe** : « Un Haut-Elfe. Je suppose que les bibliothèques de votre peuple ne suffisent pas toujours à satisfaire votre curiosité. Soyez le bienvenu, mais souvenez-vous que vous êtes ici dans les Archives de la Couronne, pas dans celles d'Ilystära. Votre autorisation ? »
- **Orc (futur)** : « Par la Couronne... Un Orc aux Archives ? Arrêtez-vous immédiatement. Je ne sais pas ce qui vous amène ici, mais vous n'irez pas plus loin sans une autorisation en règle. Et gardez vos mains bien en vue pendant que je la vérifie. »

### 7.2 Accueil positif (race), **sans invention d'exploits ou métier**

- **Humain** : « Ah, je vous reconnais ! On m'a dit du bien de vous à la Garde. La Couronne peut se féliciter de compter des résidents comme vous. Qu'est-ce qui vous conduit aux Archives ? » (adapter dynamiquement « citoyen » seulement si citoyenneté acquise ; aucune condition de citoyenneté pour MQ01).
- **Nordique** : « Ah, vous revoilà ! Les Nordiques ont mauvaise réputation chez certains de mes camarades, mais vos services parlent pour vous. Qu'est-ce qui vous conduit aux Archives ? »
- **Nain** : « Ah, vous revoilà ! On m'a dit du bien de vous à la Garde. Entre les ingénieurs et les marchands nains, votre peuple ne nous est pas étranger. Mais vous, c'est surtout votre réputation qui vous précède. Qu'est-ce qui vous amène aux Archives ? »
- **Elfe des Bois** : « Tiens, notre visiteur des forêts ! On parle de vous en bien parmi les gardes. Comme quoi, il n'y a pas que les gens de nos cités pour savoir se rendre utiles à l'Empire. Qu'est-ce qui vous amène ? »
- **Haut-Elfe** : « Ah, je vous reconnais ! Vous avez su gagner l'estime de la Garde. J'imagine que même à Ilystära, on saurait apprécier pareille conduite. Qu'est-ce qui vous conduit dans nos Archives ? »
- **Orc (futur)** : « Par la Couronne... Vous revoilà. Je dois reconnaître que vous avez gagné l'estime de plusieurs de mes camarades. Ce n'est pas tous les jours qu'un Orc reçoit pareil accueil chez nous. Qu'est-ce qui vous amène aux Archives ? »

### 7.3 Accueil négatif (race)

- **Humain** : « Vous vivez sous les lois de la Couronne, mais vous ne faites guère honneur à l'Empire. J'espère que cette visite ne nous donnera pas une nouvelle raison de nous plaindre de votre conduite. Votre autorisation ? » (n'exige pas d'être citoyen).
- **Nordique** : « Encore vous ? Voilà qui ne fera pas mentir ceux qui se méfient des gens du Nord. Les Archives ne sont pas une taverne où l'on règle ses différends à coups de poing. Votre autorisation, et tâchez de vous tenir correctement. »
- **Nain** : « Un Nain, et pas celui dont nous gardons le meilleur souvenir. Vos compatriotes qui fréquentent nos collections se montrent généralement plus respectueux de nos règlements. Vous avez une autorisation ? »
- **Elfe des Bois** : « Vous revoilà ? Je pensais que les Elfes des Bois préféraient éviter nos cités. À en juger par ce qu'on rapporte de vous, peut-être vaudrait-il mieux que vous retourniez sous vos arbres. Votre autorisation ? »
- **Haut-Elfe** : « Un Haut-Elfe qui nous cause encore des difficultés... Vous autres aimez rappeler l'ancienneté de votre civilisation, mais cela ne vous dispense pas de respecter les lois de l'Empire. Votre autorisation ? »
- **Orc (futur)** : « Vous ! Je me demandais combien de temps il faudrait avant que vous nous causiez encore des ennuis. Un Orc avec votre réputation n'a rien à faire près des Archives. Restez où vous êtes et montrez-moi votre autorisation. Au moindre problème, j'appelle du renfort. »

### 7.4 Criminalité active

**Sentinelle :** « Halte ! Vous avez des comptes à rendre à la Garde. Ne faites pas un pas de plus. »

Réutiliser le traitement existant des avis de recherche/criminalité, sans inventer une nouvelle arrestation. Des antécédents connus seuls ne sont pas équivalents à une recherche active. Vérifier que le badge de convocation ne neutralise pas la justice existante.

### 7.5 Contrôle de la convocation

**Joueur :** « J'ai reçu cette convocation des Archives. »
**Sentinelle :** « Voyons cela... »
*Examen du pli et des personnes attendues.*
**Sentinelle :** « Le sceau est authentique. Et votre nom figure bien parmi les personnes attendues. »

Questions [RETOUR] :
1. « Savez-vous pourquoi on m'a convoqué ? » → « On nous a simplement demandé de vous laisser entrer. Les raisons de votre convocation ne nous ont pas été communiquées. C'est auprès du personnel des Archives qu'il faudra vous renseigner. »
2. « Pourquoi les Archives sont-elles gardées par les Sentinelles du Cœur ? » → « Les Archives renferment des documents et des collections qui appartiennent à la Couronne. Certains sont précieux, d'autres ne sont pas destinés à être consultés librement. Notre rôle est de veiller à ce que seuls les visiteurs autorisés puissent y accéder. »
3. « Très bien. Où dois-je me rendre ? » [SUITE] → « Adressez-vous au Scribe chargé des registres. Il a été informé de votre venue et pourra vous renseigner. Vous êtes autorisé à entrer, mais cette convocation ne vous donne pas accès à l'ensemble des Archives. Respectez les indications du personnel. »

Fin :
- « Entendu, merci. » → « Vous pouvez passer. »
- « Je reviendrai un peu plus tard. » → « Comme il vous plaira. Votre autorisation reste valable. »

Dans les deux cas, **MQ01 = 100**, **MQ02 démarrée**, accès de quête aux Archives accordé ; le joueur peut revenir sans recommencer. Si lettre perdue : « J'ai été convoqué, mais je n'ai plus le courrier sur moi. » → « Votre nom, je vous prie. » → « Effectivement, votre venue est bien enregistrée. Vous pouvez vous présenter au Scribe. Gardez toutefois vos documents à portée de main à l'avenir. »

Retour pendant MQ02 : « Vous êtes toujours attendu aux Archives. Vous connaissez le chemin, adressez-vous au Scribe chargé de votre dossier. »
Sortie après MQ02 : « Votre visite est terminée, à ce que je vois. Bonne continuation. » et révocation **de la source temporaire seulement**.
Retour sans accès : « Votre précédente autorisation ne couvre plus de nouvelles visites. Si les Archives souhaitent vous recevoir de nouveau, elles nous en informeront. »

## 8. MQ02 — SCRIBE DES ARCHIVES : SCRIPT COMPLET

Lieu : grande salle principale sous le dôme, pas de deuxième étage fictif. Scribe diligent, courtois, limité par habilitation ; ne comprend pas l'origine profonde de la contradiction.

### 8.1 Accueil

**Scribe :** « Bienvenue aux Archives de la Couronne. Si vous venez consulter un registre, je vais avoir besoin de connaître l'objet de votre visite. »
1. « J'ai reçu une convocation. » → « Une convocation ? Un instant, je vous prie... Ah, oui. Votre venue m'a été signalée. C'est au sujet de votre dossier d'enregistrement. » [SUITE]
2. « On m'a demandé de me présenter pour une vérification. » → « Vous devez être la personne que nous attendions. Permettez-moi de retrouver votre dossier. » [SUITE]
3. « Savez-vous pourquoi les Archives souhaitent me voir ? » → « Pas précisément, je le crains. Une irrégularité a été signalée dans nos registres, mais je dois d'abord vérifier votre dossier avant de vous en dire davantage. » [SUITE]

### 8.2 Vérification administrative

**Scribe :** « Voyons... Votre enregistrement a été effectué à votre arrivée dans la Capitale, après votre récupération par un bâtiment de la Couronne. C'est bien cela ? »
1. « Oui. J'ai été recueilli par un dirigeable impérial. » → « C'est bien ce qui figure ici. Le rapport mentionne également que vous étiez inconscient lorsque l'équipage vous a découvert. » [RETOUR]
2. « Je ne me souviens de rien avant ce sauvetage. » → « Je vois. Votre perte de mémoire est effectivement mentionnée dans les observations qui accompagnent votre enregistrement. J'imagine que cela ne doit guère faciliter vos démarches. » [RETOUR]
3. « Pourquoi avez-vous besoin de vérifier cela ? » → « Une formalité, pour le moment. Nous devons nous assurer qu'aucune erreur ne s'est glissée dans les renseignements transmis aux Archives. » [RETOUR]
4. « Très bien. Poursuivez votre vérification. » → « Je vous remercie. Voyons maintenant ce qui a motivé votre convocation... » [SUITE]

### 8.3 Référence antérieure

*Le Scribe consulte un second registre.* « Voilà qui est curieux... »
« Pardonnez-moi. Une référence associée à votre dossier apparaît dans un registre antérieur à votre arrivée. Une incohérence avait déjà été signalée, mais je pensais qu'il s'agissait simplement d'une erreur de classement. »
1. « Antérieur ? De combien de temps ? » → « Je ne peux pas encore vous donner de date certaine sans consulter la pièce d'origine. Mais cette référence figurait déjà dans nos registres avant que le dirigeable ne vous recueille. » [RETOUR]
2. « Peut-être s'agit-il simplement d'une autre personne. » → « C'est tout à fait possible. Une référence mal retranscrite, un dossier confondu avec un autre... Ces erreurs arrivent. Toutefois, ce renvoi paraît suffisamment particulier pour mériter une vérification. » [RETOUR]
3. « Cela pourrait-il avoir un rapport avec mon passé ? » → « Je serais bien imprudent de vous l'affirmer. Je comprends que vous cherchiez des réponses, mais nous n'avons encore aucune certitude sur la nature de ce document. » [RETOUR]
4. « Avez-vous trouvé autre chose ? » → « Une autre particularité, oui... Permettez-moi de vérifier. » [SUITE]

### 8.4 Limites de la vérification

**Scribe :** « Ce n'est pas seulement une référence isolée. D'autres indications semblent également correspondre à votre dossier. »
« Malheureusement, je ne peux pas vérifier leur origine. Le document auquel renvoie cet index ne figure pas parmi les pièces auxquelles j'ai accès. »
« Je préférerais soumettre cela à l'Archivaire de la Couronne avant d'avancer une explication. »
1. « Est-ce que mon dossier pose un problème ? » → « Pas à ma connaissance. Nous avons relevé une incohérence documentaire, rien qui remette en cause votre situation auprès de la Couronne. Je préfère toutefois laisser l'Archivaire en juger. » [RETOUR]
2. « Puis-je consulter ce registre moi-même ? » → « Les références de votre enregistrement, certainement. Mais la pièce ancienne fait l'objet de restrictions que je ne suis pas habilité à lever. L'Archivaire pourra vous en dire davantage, si son contenu le permet. » [RETOUR]
3. « Très bien. Je vais attendre. » → « Je vous remercie de votre patience. Je vais transmettre ces documents à l'Archivaire. » [SUITE]

**Transmission :** « Maître, pardonnez-moi de vous interrompre. Il s'agit du dossier pour lequel nous avons demandé une convocation. J'ai retrouvé la référence signalée, mais les vérifications soulèvent quelques difficultés. »
**Archivaire :** « Faites voir... Je vois. Merci, je vais m'en charger. Approchez, je vous prie. J'aimerais vous poser quelques questions. »

L'animation du Scribe traversant physiquement une salle partagée est **à éviter** si plusieurs joueurs mènent la même scène en parallèle ; mise en scène virtuelle/dialogue ciblé acceptable, sans mouvements mondiaux incompatibles.

## 9. MQ02 — ARCHIVAIRE DE LA COURONNE : SCRIPT

Tempérament : rigoureux, réservé, curieux sans être complice immédiat ; personnage récurrent à une table de lecture sous le dôme. Investigation d'abord **documentaire et administrative**, pas thaumaturgique.

### 9.1 Premier entretien

« Prenez place, je vous prie. Mon collègue vient de m'exposer les difficultés rencontrées lors de la vérification de votre dossier. »
« Je préfère vous rassurer immédiatement : à ce stade, rien ne permet de mettre en cause la régularité de votre enregistrement auprès de la Couronne. »
« En revanche, certaines correspondances méritent que nous nous y attardions. »

1. « De quelles correspondances parlez-vous ? » → « Une référence présente dans votre dossier renvoie à une inscription antérieure à votre récupération. Il pourrait s'agir d'une erreur de classement, mais d'autres éléments semblent également correspondre. C'est ce que je souhaite vérifier. » [RETOUR]
2. « Pensez-vous que cela puisse expliquer mon amnésie ? » → « Je comprends votre interrogation. Mais je ne saurais établir un lien entre une inscription administrative et votre perte de mémoire sans disposer d'éléments supplémentaires. Ce serait aller bien au-delà de ce que ces documents nous apprennent. » [RETOUR]
3. « Pourquoi m'avoir convoqué pour une simple erreur de registre ? » → « Précisément parce que nous ne savons pas encore s'il s'agit d'une erreur. Lorsqu'un dossier présente une incohérence que nos vérifications habituelles ne permettent pas de résoudre, nous devons en rechercher l'origine. » [RETOUR]
4. « Comment comptez-vous procéder ? » → « Nous allons commencer par reprendre les circonstances de votre arrivée dans la Capitale. Les documents les plus récents seront probablement les plus simples à vérifier. » [SUITE]

### 9.2 Notice ancienne et copies

« Votre dossier a été constitué après votre récupération par un dirigeable de la Couronne. Jusque-là, rien d'inhabituel. »
« Mais au cours de son enregistrement, une correspondance a été relevée avec une notice plus ancienne. Celle-ci reprend notamment une description qui présente certaines similitudes avec la vôtre. »
« La difficulté vient de son origine. Cette notice renvoie elle-même à d'autres registres, dont certains ont été recopiés à plusieurs reprises. Je peux dater certaines copies, mais pas la source dont elles proviennent. »

Questions répétables :
1. « En quoi cette personne me ressemble-t-elle ? » → « Le signalement mentionne notamment votre peuple et plusieurs caractéristiques physiques concordantes. Cela mérite notre attention, mais ce n'est certainement pas suffisant pour établir une identité. Les descriptions anciennes sont parfois imprécises, surtout lorsqu'elles ont été retranscrites par plusieurs générations de scribes. »
2. « De quand date ce document ? » → **Réponse adaptée à la race, ci-dessous.**
3. « Pourquoi mon dossier renvoie-t-il à ces anciennes archives ? » → « C'est précisément ce que je cherche à comprendre. Notre administration a repris une référence plus ancienne, mais les pièces conservées ne permettent pas de retrouver clairement la première personne qui l'a établie. Il peut s'agir d'une erreur répétée au fil des copies. Toutefois, la correspondance avec votre signalement rend cette explication moins satisfaisante. »
4. « Vous pensez que j'ai déjà été enregistré ici ? » → « C'est une possibilité, mais je ne voudrais pas vous induire en erreur. Rien ne prouve encore que ces inscriptions vous concernent personnellement. Il serait tout aussi imprudent de les écarter sans poursuivre les vérifications. »
5. « Que pouvons-nous faire maintenant ? » → « Reprendre les faits dont nous sommes certains. À commencer par les circonstances de votre récupération. Vous avez été recueilli par le capitaine Althéon Brumeforge, si je ne me trompe pas. J'aimerais que nous examinions plus précisément ce qui a été consigné lors de ce voyage. » [SUITE]

Réponses à 2 selon race (sans présumer de l'âge réel, du métier ou de l'origine du protagoniste) :
- **Humain ou Nordique** : « L'une des copies est considérablement antérieure à votre arrivée. Pour une personne de votre peuple, une telle ancienneté soulèverait évidemment des questions, si nous pouvions établir qu'il s'agit bien de vous. »
- **Nain** : « Votre peuple possède une longévité qui rend certaines comparaisons moins invraisemblables qu'elles ne le seraient pour un Humain. Mais je ne connais ni votre âge, ni celui de l'individu décrit. L'ancienneté ne nous permet donc pas de conclure. »
- **Elfe des Bois** : « La longévité des Elfes rend délicate toute conclusion fondée sur l'âge d'un document. Une même personne peut traverser des périodes que nos archivistes humains sépareraient volontiers en plusieurs générations. »
- **Haut-Elfe** : « Compte tenu de la longévité de votre peuple, même une archive particulièrement ancienne ne constituerait pas, à elle seule, une anomalie. Ce qui me préoccupe davantage, c'est cette chaîne de références dont nous ne connaissons plus l'origine. »
- **Orc futur** : « Je ne dispose d'aucun élément fiable permettant de déterminer l'âge de l'individu décrit. Je préférerais ne pas tirer de conclusions sur la seule ancienneté de cette inscription. »

**Lore obligatoire** : ancienne notice impériale issue d'une copie de sources successivement plus anciennes, source initiale introuvable et impossible à dater ; sa fonction initiale n'est pas connue. Le signalement ressemble partiellement au personnage Nexus : race et caractéristiques *effectivement sélectionnées*, sans ajout d'une cicatrice, d'un âge, d'un visage ou de lignée imposés. L'Archivaire ne conclut jamais qu'il s'agit vraiment du joueur. Ne pas montrer l'ancrage, les mécanismes des Anciens, la Faille ou la cosmologie secrète à cette étape. Si la lecture de certains paramètres cosmétiques Nexus n'est pas disponible, utiliser une formulation générique sincère, signaler la limitation, et ne pas inventer de correspondance précise.

### 9.3 Rapport de récupération et conclusion MQ02

« Revenons à ce que nous pouvons établir avec certitude. Vous avez été recueilli par un dirigeable impérial qui revenait d'Avaleiv, sous le commandement du capitaine Althéon Brumeforge. »
« Le rapport transmis aux Archives confirme votre état lors de la récupération. En revanche, il reste assez sommaire sur les circonstances de votre découverte. »

Questions :
1. « Qu'indique exactement ce rapport ? » → « Que vous avez été découvert inconscient, seul, sur un relief isolé. L'équipage vous a pris en charge avant de poursuivre sa route vers la Capitale. Le reste concerne principalement votre état à l'arrivée et les formalités de votre enregistrement. » [RETOUR]
2. « Pourquoi n'y a-t-il pas davantage d'informations ? » → « Ce document était destiné à justifier votre prise en charge et votre enregistrement. Il ne constitue pas un compte rendu détaillé de la traversée. Les observations de navigation sont généralement consignées séparément. Elles ne sont pas nécessairement jointes aux dossiers individuels. » [RETOUR]
3. « Vous pensez que le capitaine pourrait en savoir davantage ? » → « Il a supervisé votre récupération. Il pourrait se souvenir de détails qui n'ont jamais figuré dans le rapport. Son journal de bord devrait également nous permettre de préciser l'itinéraire du dirigeable. » [RETOUR]
4. « Je vais essayer de retrouver le capitaine. » → « C'est une démarche raisonnable. Je poursuivrai de mon côté l'examen des anciennes références. Si vous parvenez à obtenir des renseignements plus précis sur le lieu de votre récupération, revenez me voir. Nous pourrons les confronter à nos cartes et à nos documents. » [SUITE]

Ajouter en conclusion, avec sa discrète curiosité :
« Et, si vous me permettez une remarque... Ce dossier est particulièrement inhabituel. J'aimerais autant que vous comprendre comment ces références ont pu se retrouver associées à votre nom. »

Un Scribe peut signaler aux Sentinelles une **permission de retour** auprès de l'Archivaire après l'enquête auprès d'Althéon. Les Archives **centralisent immédiatement les documents administratifs de la Couronne**, pas de « registre portuaire indépendant ». Le capitaine garde un **exemplaire de travail** de son journal de navigation ; les Archives conservent leurs copies officielles, dont l'Archivaire ne dispose pas forcément immédiatement dans son dossier individuel.

**MQ02 terminée à la fin de ce dialogue, MQ03 ouverte avec objectif « Retrouver le capitaine Althéon Brumeforge au Port de la Haute Capitale ».** L'accès de MQ02 reste actif jusqu'à sortie, puis sa seule source temporaire est retirée. Ce droit est redonné lorsque MQ03 atteint l'étape de restitution à l'Archivaire.

## 10. MQ03 — LE JOURNAL DE BORD : SCRIPT

### 10.1 Retrouvailles avec Althéon

**Althéon :** « Tiens donc ! Notre rescapé. Je ne pensais pas vous revoir si tôt. Vous vous êtes fait à la vie dans la Capitale ? »

1. « Plutôt bien, merci. » → « Tant mieux. Après les circonstances de votre arrivée, voilà une nouvelle qui fait plaisir à entendre. » [RETOUR]
2. « Je cherche toujours à comprendre ce qui m'est arrivé. » → « Je m'en doute. Perdre ses souvenirs, ça ne doit pas être une mince affaire. J'aurais aimé pouvoir vous en dire davantage à bord. » [RETOUR]
3. « Les Archives m'ont envoyé vous poser quelques questions. » → « Les Archives ? Voilà qui est inattendu. Qu'est-ce qu'elles peuvent bien vouloir savoir de notre traversée ? » [SUITE]

### 10.2 Pourquoi le journal ?

**Joueur :** « Ils ont trouvé des incohérences dans mon dossier. L'Archivaire pense que votre journal de bord pourrait nous aider à retrouver l'endroit où vous m'avez découvert. »
**Althéon :** « Je vois. Le rapport de récupération était assez succinct, c'est vrai. Mais le journal de bord, lui, devrait contenir les relevés de navigation. Il faudra que je remette la main dessus. Qu'espérez-vous y trouver exactement ? »

1. « Je voudrais savoir précisément où vous m'avez trouvé. » → « Je comprends. Je me souviens du secteur, mais pour la position, il faudra consulter les relevés. Nous étions sur le retour d'Avaleiv. » [RETOUR]
2. « Vous n'avez vraiment rien remarqué d'inhabituel ? » → « Pas de quoi expliquer votre état, non. Notre vigie vous a aperçu sur un relief isolé, et nous sommes descendus vous récupérer. Vous étiez seul, inconscient. J'ai surtout veillé à ce qu'on vous ramène en vie. Je n'ai pas eu l'occasion de mener une enquête sur place. » [RETOUR]
3. « Pourrions-nous consulter votre journal de bord ? » → « Je ne vois pas d'objection à retrouver les relevés de cette traversée. Laissez-moi regarder ce que nous avons conservé. » [SUITE]

### 10.3 Consultation du journal

*Althéon ouvre son exemplaire de travail.*
« Voyons... Avaleiv, livraison effectuée, reprise du cap vers la Capitale... Ah, nous y voilà. Le jour où nous vous avons recueilli. »
« La vigie vous a aperçu pendant notre retour, dans un secteur où les îles sont assez nombreuses. J'ai noté notre position approximative, mais pas les coordonnées exactes du lieu où nous avons posé pied. »

1. « Vous pourriez me montrer où cela se trouve ? » → « À peu près, oui. Voyez cette partie de notre itinéraire. Nous étions au large de cette portion du littoral. Mais déterminer l'île exacte avec ces seuls relevés... Ce serait vous donner une certitude que je n'ai pas. » [RETOUR]
2. « Vous souvenez-vous de quelque chose que vous n'avez pas écrit ? » → « Quelques détails du terrain, surtout. Je me souviens d'une hauteur rocheuse et d'un endroit peu accueillant. Nous n'y sommes pas restés longtemps. Vous étiez inconscient. Nous avions déjà notre cargaison livrée et une longue route derrière nous. J'ai jugé préférable de vous mettre à l'abri plutôt que de fouiller les environs. » [RETOUR]
3. « Pourquoi ne pas avoir consigné ces détails ? » → « Parce que je rédigeais un journal de navigation, pas le récit d'une expédition. J'y ai porté les renseignements utiles à notre traversée et à votre récupération. Le reste ne me paraissait pas important sur le moment. » [RETOUR]
4. « Je pense avoir de quoi poursuivre mes recherches. » → « Alors je vous souhaite de trouver ce que vous cherchez. Si vous comptez retourner là-bas, renseignez-vous auprès de navigateurs qui connaissent ces îles. Ils sauront peut-être reconnaître les lieux mieux que moi. » [SUITE]

**Important :** ne pas identifier le monolithe des Anciens ni l'île précise dans ce dialogue. Althéon est un capitaine expérimenté qui sait que ces vestiges existent, mais il n'en a pas signalé à cet endroit et nous ne voulons pas lui inventer une observation. Les relevés de navigation sont approximatifs.

**Fin étape Althéon :** ouvrir l'objectif « Retourner auprès de l'Archivaire aux Archives de la Couronne » et préparer le retour autorisé. Le journal de Clément enregistre les faits ; pas besoin de donner au joueur un deuxième livre de navigation. Une visite chez les Sentinelles est donc nécessaire.

### 10.4 Deuxième contrôle des Sentinelles

**Sentinelle :** « Vous revoilà. Vous avez donc trouvé ce que vous cherchiez ? »

1. « J'ai retrouvé le capitaine. Je dois transmettre ses renseignements à l'Archivaire. » → « Ah, l'affaire du dirigeable. Un Scribe nous a signalé que vous pourriez revenir. L'Archivaire vous attend, vous pouvez passer. » [ACCÈS]
2. « Vous avez été prévenu de mon retour ? » → « En effet. Les Archives nous ont fait parvenir une nouvelle autorisation à votre nom. Il semblerait que votre dossier demande davantage d'attention que prévu. » [RETOUR]
3. « Très bien, je vais le rejoindre. » → « Allez-y. Et tâchez de satisfaire sa curiosité, il n'est pas habituel de voir les Archives rappeler deux fois la même personne aussi rapidement. » [ACCÈS]

Ne pas répéter l'accueil racial comme si la Sentinelle rencontrait pour la première fois la même personne. Réputation/criminalité s'appliquent néanmoins selon les états effectifs.

### 10.5 Entretien final avec l'Archivaire

**Archivaire :** « Vous voilà de retour. J'imagine que vous avez pu rencontrer le capitaine Brumeforge ? »
**Joueur :** « Oui. Il m'a communiqué plusieurs renseignements sur les circonstances de mon sauvetage. »
**Archivaire :** « Fort bien. Voyons ce que nous pouvons en tirer. »
*Compare les relevés aux cartes des Archives.*
« Le dirigeable revenait donc d'Avaleiv... Compte tenu de son itinéraire et des indications du capitaine, plusieurs îles pourraient correspondre. »
« Toutefois, en confrontant ces renseignements à nos cartes, je pense pouvoir réduire quelque peu le secteur de recherche. »
« Les environs de Havre-Fort me semblent constituer le meilleur point de départ. Les navigateurs de cette région connaissent certainement mieux ces îles que nous. »

1. « Pourquoi Havre-Fort ? » → « Sa situation géographique, principalement. Les relevés du capitaine nous orientent vers les eaux de cette région. Il serait plus judicieux d'interroger des marins qui les parcourent régulièrement que de chercher ici des renseignements trop anciens ou incomplets. » [RETOUR]
2. « Comment puis-je m'y rendre ? » → « Vous pouvez emprunter la route terrestre, si vous préférez voyager par vos propres moyens. Autrement, des navires assurent des traversées depuis Lion-Port jusqu'à Havre-Fort, moyennant paiement. À vous de choisir ce qui vous convient le mieux. » [RETOUR]
3. « Et les anciennes références de mon dossier ? » → « Je n'ai pas encore trouvé leur origine. Je compte poursuivre les recherches pendant votre absence. Il est possible que certains documents aient échappé à nos premiers examens. Pour le moment, les circonstances de votre récupération demeurent notre piste la plus concrète. » [RETOUR]
4. « Très bien. Je vais me rendre à Havre-Fort. » → « Je vous recommande de commencer par le port. Les marins pourront peut-être reconnaître les lieux décrits par le capitaine. Soyez prudent si vous envisagez de vous aventurer sur ces îles. Nous ne savons pas encore ce que vous pourriez y trouver. Et lorsque vous aurez obtenu des renseignements supplémentaires, revenez me voir. Je serais particulièrement intéressé de connaître la suite de cette affaire. » [SUITE FIN MQ03]

**Fin : MQ03 = 100 ; MQ04 démarre immédiatement**, objectif « Se rendre à Havre-Fort pour enquêter sur les îles de la région ». Marqueur et waypoint Xaero au **port de Havre-Fort**, emplacement réel à identifier et valider, jamais sur le monolithe ou l'île. À l'issue de MQ03, l'accès temporaire aux Archives est retiré lors de la sortie ; ne pas effacer un droit de rang, de faveur ou d'une autre quête.

## 11. ACCÈS AUX ARCHIVES : PRIORITÉ `3` SANS PERTE DE DROITS

L'utilisateur souhaite que `ACCESS_* = 3` soit la valeur **prioritaire** d'accès accordé par une quête (potentiellement de longue durée), mais les autorisations de rang `2` et individuelles `1` restent existantes ; les laissez-passer chronométrés (`CAP_PASS_ZONE`, `CAP_PASS_TIMER` / `CAP_PASS_END` suivant version) sont **une mécanique séparée**.

Ne pas simplement appliquer `scoreboard set ACCESS_ARCHIVES 3` puis `set 0` à la sortie : cela efface une autorisation existante. Construire un mécanisme de **sources d'accès cumulables** (droit permanent, rang, jetons de quêtes par émetteur, délai éventuel) et calculer l'accès effectif ; lorsque la permission MQ02 expire, retirer uniquement le jeton MQ02 et recalculer. Même chose pour MQ03. Le `3` prend priorité sans effacer le `2` sous-jacent. Préserver les privilèges déjà acquis par le core, la citoyenneté et les autorisations du Tribunal. Inspecter toutes les fonctions existantes d'attribution/révocation et les tests de passages avant modification.

Une Sentinelle gardant un accès possède déjà un timer pour la reconduite après « Suivre le garde » ou « Tenter un arrangement ». Suspendre proprement la contrainte pendant les dialogues de quête à plusieurs choix, puis la reprendre si la conversation est abandonnée sans autorisation ; l'annuler dès autorisation. Aucun ancien timer ne doit déplacer le joueur après validation de MQ01 ou MQ03. Prévoir les cas d'entrée par téléportation et de déconnexion à l'intérieur, sans rupture de droit préalable.

## 12. INTÉGRATION JOURNAL DE CLÉMENT / MARKERS / XAERO

Étudier le JAR courant de `haute-capitale-quetes` et ses schémas JSON ; ne pas inventer de champs non pris en charge. Pour `mq_01_convocation`, `mq_02_registres`, `mq_03_journal_bord`, créer les fiches et leurs objectifs `SCOREBOARD`, prérequis, descriptions, cibles et indications géographiques. **MQ04** reçoit uniquement son premier objectif et son marqueur à Havre-Fort.

Marqueurs `!` (quête disponible) et `?` (interaction pertinente/remise) au-dessus des **PNJ de quête pertinents**, via les mécanismes `text_display` et `hcq_mrk_*` existants ; priorité contrôlée si plusieurs quêtes utilisent le même PNJ. Marqueurs et dialog listeners actualisés lorsque le personnage Nexus change et lorsqu'une quête évolue ; les PNJ permanents ne sont pas dupliqués pour différents joueurs.

Xaero : réutiliser le pont de la quête suivie (nom historique interne `quete_suivie`, à vérifier dans JAR actif). **Xaero's Minimap et World Map sont tous deux installés.** Le waypoint doit accompagner l'objectif actif ; lorsque MQ04 devient active, la définir comme quête suivie si compatible avec les préférences du joueur, ou expliquer le comportement choisi, puis positionner le waypoint à Havre-Fort. Ne pas laisser le waypoint de l'Archivaire après fin MQ03. Ne pas inventer de coordonnées géographiques ; les lire sur la carte et les confirmer.

## 13. ISOLATION DES PERSONNAGES NEXUS — OBLIGATION

Le même UUID de compte peut avoir deux personnages différents. Écrire et vérifier la **clé de progression liée à l'identifiant stable du personnage Nexus actif**, tout en conservant l'UUID Minecraft pour les communications réseau normales. Réutiliser le mécanisme de sauvegarde/restauration de profils effectivement implémenté par le Nexus courant ; compléter la politique de persistance des nouveaux `hcq_*`, `hch_*`, `hcf_*` et des états `CAP_POINTS_TOTAL` / accès sans doubler les stockages. Le journal b6 peut mettre en cache sous UUID Minecraft, mais il doit être invalidé et rafraîchi après changement de personnage ; idem marqueurs, dialogues, états EasyNPC et waypoint Xaero.

Tests obligatoires : A obtient MQ03, B sur le même compte n'a pas MQ01 ; passage A→B→A restitue correctement score, lettre/identité, accès, waypoint, marqueurs et messager. Deux comptes distincts peuvent converser simultanément avec le Scribe et l'Archivaire sans se voler choix/flags ; les actions d'un joueur ne modifient pas les autres. Reconnexion en plein dialogue ; pas de double gain ni d'impasse.

## 14. VÉRIFICATION QUALITÉ ET RECETTE

**Audit statique** : scan des JSON, `.mcfunction`, NBT et chemins de commandes ; existence de chaque fonction appelée ; unicité des IDs et triggers ; syntaxe des composants, des `return`, `execute`, `tellraw`, `clickEvent`, de l'`ON_INTERACTION` EasyNPC ; validations avec la version réelle du JAR ; comparaison de toutes les entrées de ZIP ; pas de doublons PNJ ; aucun waypoint vers une île encore inconnue ; aucune fuite de données de quête par UUID entre profils Nexus.

**Recette Minecraft/Fabric** : prologue jusqu'au statut Résident, 4→5 points (dépense conserve cumul), activation du messager en cité impériale seulement, contenu et remise une seule fois de la convocation, accès avec et sans lettre, six races x 3 attitudes de la Garde + criminalité active (Orc futur si non jouable, test simulé), trois sections du Scribe et deux entretiens de l'Archivaire, toutes questions répétables, interruption/reconnexion, ouverture/fermeture de MQ01–MQ04, sortie/retour aux Archives, `ACCESS_ARCHIVES` 0/1/2/3, droit judiciaire, gardes et timers, lettre/récompenses non duplicables, tab/score progression si déjà intégré, textes/`!/?`/Xaero, duo de comptes + duo de personnages Nexus. Vérifier les collisions de quêtes avec les 66 missions migrées et avec les dialogues des PNJ partagés.

**Performance :** mesures avec un PNJ puis plusieurs PNJ, pas de nouveaux ticks globaux coûteux, scripts activés uniquement par événements ou contrôles raisonnablement espacés. **Test de non-régression des gardes :** contrôler l’entrée/sortie sans convocation, les avertissements, la reconduite, l’arrangement, la criminalité, les agressions, la bonne autorisation temporaire, la réputation, deux joueurs simultanés et les timers après fermeture anticipée d’un dialogue ; comparer avec un garde du Cœur opérationnel. Ne pas intégrer maintenant la personnalisation 3D Nexus sur PNJ sans profilage et acceptation explicite ; permettre la substitution de presets plus tard.

Résultats de chaque test étiquetés **« statique »**, **« banc »**, **« serveur Fabric »**, ou **« non effectué »** ; aucune déclaration « fonctionne en jeu » sans essai réel. Toute difficulté non résolue doit être documentée avec sa reproduction.

## 15. LIVRAISON & CONTRÔLES DE RÉCEPTION

Fournir :

1. Archives ZIP du core et de `hc_quetes` basées sur les versions de production fournies, cohérentes avec la norme v2 ; JAR du journal/Nexus/EasyNPC seulement si une modification prouvée est nécessaire et réalisée, avec changement minimal et rétrocompatibilité étudiée.
2. **Une commande de pose par PNJ inédit, à exécuter sur place**, les fonctions de liaison des PNJ existants (surtout les Sentinelles et Althéon), les fonctions de diagnostic et de retour arrière, et un guide de placement individuel. **Aucune commande unique de déploiement mondial n’est demandée sans coordonnées validées.**
3. PNJ EasyNPC techniquement configurés **dans les limites prouvées du format** (type, tags, `ON_INTERACTION`, dialogue et comportements requis) ; laisser **Clément gérer les skins/apparences**, sans les remplacer. Démontrer le maintien des fonctions de garde pour les Sentinelles ; expliquer les ajustements manuels nécessaires.
4. Fiches JSON MQ01, MQ02, MQ03 et objectif initial MQ04 ; journaux, textes, marqueurs, waypoint Xaero et déclarations d'étapes, fichier `docs/quests/registry.csv` mis à jour.
5. Liste des chemins modifiés/ajoutés, diff lisible, intégrité des quêtes v2 déjà migrées, SHA-256, rapport de tests, commandes de test admin, procédure de rollback.
6. Démonstration reproductible de **deux personnages du même compte** à progression différente et de **deux joueurs simultanés**, si accès au serveur de test réellement disponible.

**NE PAS** déployer ni écraser les fichiers du serveur de production sans accord. **NE PAS** considérer « un NPC qui apparaît » comme preuve que les dialogues, états, accès et marqueurs fonctionnent. **NE PAS** introduire de révélations narratives non validées sur les Anciens, le monolithe, l'ancrage, la Faille, la datation originale de la notice ou les origines du personnage.

## 16. APPROCHE DE TRAVAIL ATTENDUE DE WORK

1. Diagnostiquer versions, migration v2, sources autoritaires et commandes EasyNPC ; faire un tableau des dépendances et des zones/PNJ dont les coordonnées manquent.
2. Lorsque les fichiers sont présents et l'architecture techniquement vérifiée, **implémenter réellement** par lots cohérents sans s'arrêter à des recommandations : état des quêtes, créations des PNJ, dialogues, ponts vers journal/Xaero/Nexus, accès.
3. Effectuer les tests statiques, puis un serveur de test si les outils le permettent ; corriger les échecs ; rendre compte de toute limite.
4. Fournir les artifacts installables et un dossier de recette. Demander une décision de l'utilisateur **seulement** pour un manque de données indispensable, une modification importante du JAR ou une divergence avec les dialogues/canon validés.

**Résultat final attendu : séquence I MQ01–MQ03 jouable, nouveaux PNJ créables individuellement sur place par une seule fonction chacun, Sentinelles préservant intégralement les gardes de passage du Cœur, skins gérés par Clément, journal intégré et progression isolée par personnage Nexus, prête pour validation sur serveur de test.**
