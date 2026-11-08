# INDIVIS — NORME DE DÉVELOPPEMENT DES QUÊTES
## Contrat de nommage, données, compatibilité Nexus / core / EasyNPC / journal / Xaero

**Version du guide : 1.0 — 19 octobre 2026**  
**Destinataire : Clément, auteurs de datapacks, développeurs du core et du mod de quêtes.**  
**Statut : proposition de norme à adopter pour les nouveaux contenus ; aucun fichier de production modifié.**  
**Objet : toutes les nouvelles quêtes du serveur, pas uniquement MQ01–MQ04.**

### Lecture obligatoire : ce qui existe et ce qui est proposé

- **[OBSERVÉ]** : constat vérifié dans les trois archives reçues, dont les noms figurent en annexe.
- **[NORME PROPOSÉE]** : convention que l'équipe doit **valider avant de l'appliquer**. Une convention décrite ici n'est pas automatiquement une fonctionnalité actuellement implémentée.
- **[À AUDITER]** : détail qui dépend de la version réellement en production (le core peut être en 1.5.7 et Nexus en PRE34 ou supérieur).
- **[HÉRITAGE]** : identifiant, comportement ou contenu déjà présent : **conserver tel quel**, sauf migration explicitement conçue et testée.

**Principe fondamental : on normalise immédiatement les nouvelles quêtes ; on ne renomme pas en masse les anciennes quêtes.** Les ID sont des clés de persistance, pas des titres décoratifs.

---

# I. État réel de l'existant

## I.1 Sources et périmètre du contrôle

1. `capitale_core_1.5.6-RC9BC_INTERPELLATION_CONTINUITY_FULL(1).zip` : fonctions du prologue, scoreboards `QUEST_*`, rangs, accès `ACCESS_*`, réputation, gestion des dialogues et des gardes.
2. `hc_quetes-0.1.0.b21_LIVRES_LORE_RECANON_v16_1(2).zip` : 66 définitions de quêtes sous `data/hc_quetes/hcq_quests/`, logique de Clément, interactions, livres, PNJ et marqueurs.
3. `haute-capitale-quetes-0.1.0+1.21.11.b6(2).jar` : mod de journal et intégrations de visibilité / waypoints.

[OBSERVÉ] Parmi les 66 quêtes, il existe **10 `MAIN`, 42 `SIDE`, 7 `REPEATABLE` et 7 `COMBAT`**. Les définitions portent actuellement des ID `core_*`, `hcj_*` et `mc_*`. **Attention : le préfixe hérité `mc_` n'est pas l'indicateur d'une quête principale !** Il contient plusieurs quêtes secondaires et de combat.

[OBSERVÉ] Les états sont hétérogènes : `QUEST_SPAWN`, `QUEST_CITOYENNETE`, `hcq_j01`, `hcq_h`, `hcq_p`, etc. Le moteur de journal lit les **scores désignés explicitement dans chaque JSON**, et non un nom de scoreboard déduit automatiquement de l'ID de quête.

[OBSERVÉ] Les champs présents dans les JSON incluent `id`, `title`, `type`, `giver`, `zone`, `description`, `can_abandon`, `rewards`, `source`, `objectives`, souvent `prerequisites`, parfois `next_quest`. Les objectifs observés sont : `TALK_TO_NPC`, `RETURN_TO_NPC`, `INTERACT`, `REACH_LOCATION`, `KILL`, `BOSS_KILL`, `COLLECT`, `DELIVER_ITEM`, `CUSTOM`.

[OBSERVÉ] Le datapack utilise des marqueurs `text_display` (`!` et `?`), des tags de PNJ `hcq_pnj_*`, des tags `hcq_mrk_*`, le routeur `hcq_pnj` et le routeur `hcq_choix`. Le JAR contient des composants `QuestVisibility` et `XaeroBridge`. Xaero's Minimap **et** World Map sont installés selon le propriétaire du serveur.

[À AUDITER] Le fonctionnement des caches de visibilité et la persistance Nexus **par personnage** doivent être revalidés avec les JAR actifs, notamment les correctifs QUESTSYNC, ainsi qu'en serveur multijoueur. Aucun essai réel de cette nouvelle norme n'a été réalisé ici.

---

# II. Principes de compatibilité obligatoires

1. **Un seul ID immuable par quête** : ne jamais recycler un ancien ID pour une autre histoire, même après suppression d'une quête.
2. **Un seul propriétaire autoritaire de l'état** : un module écrit les étapes ; le journal les lit. Deux systèmes ne maintiennent jamais deux vérités indépendantes pour la même quête.
3. **Chaque valeur persistante appartient au personnage Nexus actif**, pas simplement à l'UUID Minecraft.
4. **Pas de rupture rétroactive** : ne pas modifier les anciens préfixes ni les anciens états sans migration et sauvegarde de référence.
5. **Les fichiers texte ne contrôlent pas la progression** : titres, lore, descriptions, cartes, marqueurs et ID techniques remplissent des fonctions distinctes.
6. **Aucune récompense, progression ou téléportation en lecture seule** : rouvrir une question, consulter le journal, afficher un marqueur ou restaurer un personnage ne déclenche aucun effet de quête.
7. **Pas de comportement mondial individuel** : une scène jouée par A ne déplace pas le Scribe, l'Archivaire ou Althéon pour tous les autres joueurs.
8. **Les permissions préexistantes survivent aux quêtes** : une révocation de visite ne retire ni les accès de rang ni les droits accordés autrement.
9. **Une découverte reste une découverte** : n'afficher ni dans le texte ni dans le waypoint la position exacte d'un lieu que l'histoire ne révèle pas encore.
10. **Validation en jeu distincte de la validation statique** : ne jamais déclarer « multijoueur vérifié » après une simple lecture des archives.

---

# III. Nomenclature canonique des nouvelles quêtes

## III.1 Écriture générale

[NORME PROPOSÉE]

- Identifiants de code **ASCII minuscules**, chiffres et `_` uniquement : `^[a-z][a-z0-9_]*$`.
- Aucun accent, espace, tiret, apostrophe, ponctuation ou caractère de mise en forme dans les ID.
- Français autorisé et encouragé dans `title`, `description`, objectifs et dialogues ; **les ID techniques ne changent jamais quand le titre change**.
- Ne pas encoder le nom d'un développeur, la date, la version, le type de récompense ou une coordonnée dans un ID.
- Un code géographique reste stable même si le lieu est renommé dans le lore ; conserver l'alias dans le registre central.

## III.2 Familles d'ID (`id` du JSON)

| Famille | `type` du journal | Forme canonique | Exemple | Usage |
|---|---|---|---|---|
| Quête principale | `MAIN` | `mq_NN_slug` | `mq_01_convocation` | Ordre canonique de la grande intrigue |
| Quête secondaire | `SIDE` | `sq_ZZ_NNN_slug` | `sq_hf_001_cartes_marines` | Mission non principale, éventuellement en chaîne |
| Quête de combat | `COMBAT` | `cq_ZZ_NNN_slug` | `cq_lp_001_patrouille` | Contenu rangé en catégorie combat |
| Quête répétable | `REPEATABLE` | `rq_ZZ_NNN_slug` | `rq_hf_001_livraison` | Répétition contrôlée avec remise à zéro prévue |

**`NN`** : deux chiffres, ordre narratif (`01`, `02`, …). **`NNN`** : trois chiffres dans la zone/famille, sans réutilisation (`001`, `002`, …). **`ZZ`** : code court de localisation/ligne, réservé dans un registre partagé. **`slug`** : nom de fonction court et descriptif, immuable.

**Exemples réservés pour la conception actuelle** (pas encore créés dans le datapack) :

| ID JSON proposé | Code interne | Titre affiché |
|---|---|---|
| `mq_01_convocation` | `mq01` | MQ01 — La Convocation |
| `mq_02_registres` | `mq02` | MQ02 — Les Registres |
| `mq_03_journal_bord` | `mq03` | MQ03 — Le Journal de bord |
| `mq_04_havre_fort` | `mq04` | MQ04 — **titre narratif non défini**, ID technique d'exemple à confirmer |

**Important** : les anciennes quêtes classées `MAIN` pour le prologue et la citoyenneté restent dans leur espace d'identifiants `core_*`. La citoyenneté n'est **pas** un prérequis de MQ01. Ne pas remplacer `core_citoyennete` par `mq_*`, ne pas l'enchaîner automatiquement sur MQ01 et ne pas fabriquer une seconde version de la même quête.

## III.3 Table des codes géographiques

[NORME PROPOSÉE — codes à réserver dans le registre, sous réserve de confirmation géographique]

| Code | Localisation de travail | Exemple d'usage |
|---|---|---|
| `hc` | Haute Capitale | `sq_hc_001_*` |
| `lp` | Lion-Port | `sq_lp_001_*` |
| `hf` | Havre-Fort | `sq_hf_001_*` |
| `vc` | Vieille Côte | `sq_vc_001_*` |
| `ba` | Bas-Anneaux de la Capitale | `sq_ba_001_*` |

Les autres codes doivent être **attribués dans le registre avant création**, jamais improvisés dans une PR. Une chaîne qui se déroule dans plusieurs régions conserve son code d'origine ou un code de ligne spécialement réservé ; elle ne change pas d'ID à chaque déplacement.

## III.4 ID public, code court et titre : ne pas confondre

Exemple :

- ID journal immuable : `sq_hf_001_cartes_marines`.
- Code court interne : `sqhf001`.
- Scoreboard d'état : `hcq_sqhf001`.
- Titre RP affiché : « Les cartes des îles » — modifiable sans migration.
- Chemin de fonctions : `hc_quetes:quests/sqhf001/...`.

**Ne jamais utiliser le titre français comme clé de stockage, identifiant PNJ ou nom de dossier technique.**

## III.5 Registre obligatoire des identifiants

Tenir un fichier partagé versionné, **avant** d'ajouter les quêtes : `docs/quests/registry.csv` (chemin recommandé hors datapack, au niveau du dépôt de développement). Colonnes requises :

`quest_id,code,type,zone_code,arc_id,state_objective,source_owner,status,first_release,notes`

- `source_owner` = `hc_quetes`, `capitale` (pour une quête héritée exposée dans le journal) ou autre namespace déclaré.
- `status` = `reserved`, `implemented`, `released`, `deprecated` ; **ne jamais supprimer les lignes `deprecated`**.
- `arc_id` est une métadonnée du **registre de développement**, pas un champ JSON du journal b6.
- La clé primaire est `quest_id` ; `code` et `state_objective` sont eux aussi uniques.
- Ajouter les relations de `prerequisites` / `next_quest` dans un second audit de graphe ; vérifier l'absence de dépendances circulaires.

Le fichier séparé `Indivis_Registre_Quetes_Existantes_et_Proposees.csv` accompagne ce guide avec les 66 quêtes présentes à la date du contrôle. **Il n'est pas un plan de renommage**.

---

# IV. Nomenclature des scoreboards et progression

## IV.1 Scoreboard principal d'une quête

[NORME PROPOSÉE] Pour toute **nouvelle** quête du datapack de Clément :

`hcq_<code>`

Exemples : `hcq_mq01`, `hcq_mq02`, `hcq_mq03`, `hcq_sqhf001`.

Le **même** scoreboard doit être référencé dans :

1. la fonction qui écrit la progression ;
2. le JSON `source.active`, `source.turn_in` (si pertinent), `source.completed` ;
3. les `objectives[].done` du journal ;
4. les conditions d'interaction et les marqueurs ;
5. les vérifications de persistance Nexus et les tests.

Ne pas créer en parallèle `QUEST_MQ01` **et** `hcq_mq01` comme deux états autonomes. Si un callback venant du core est nécessaire, celui-ci doit appeler l'unique gestionnaire d'état : `function hc_quetes:quests/mq01/...`.

Pour les quêtes **héritées du core**, leur état `QUEST_*` reste la source autoritaire : le JSON `core_*` du journal doit le lire, sans miroir autonome. Pour les quêtes **héritées de Clément**, conserver les objectifs `hcq_h`, `hcq_p`, `hcq_j01`, etc., jusqu'à migration explicite.

### Politique de longueur et d'unicité

Par **convention de projet**, garder les **nouveaux noms de scoreboard à 16 caractères maximum**, pour faciliter leur audit et la compatibilité des outils. Ce n'est pas ici une affirmation sur la limite exacte de la version de Minecraft utilisée. Toute exception doit figurer dans le registre et être testée sur Fabric 1.21.11.

## IV.2 Suffixes réservés aux états secondaires

| Fonction | Forme | Exemple | Sémantique |
|---|---|---|---|
| État de la quête | `hcq_<code>` | `hcq_mq02` | Progression principale |
| Sous-objectif / compteur | `hcq_<code>_oN` | `hcq_mq02_o1` | Valeur d'un objectif clairement nommé dans le registre |
| Choix narratif persistant | `hcq_<code>_ch` | `hcq_mq02_ch` | Décision irréversible ou lue plus tard |
| Marqueur de branche | `hcq_<code>_br` | `hcq_mq03_br` | Branche exclusive, si nécessaire |
| État local à reprendre | `hcq_<code>_fl` | `hcq_mq01_fl` | Drapeau de scène persistante, documenté |

Pour les codes courts plus longs, vérifier la limite conventionnelle : par exemple `hcq_sqhf001_o1` et `hcq_sqhf001_ch` restent concis. Un code et un suffixe ne doivent **jamais** être tronqués au hasard : toute nouvelle abréviation est consignée dans le registre.

**`_o1` n'est pas une invitation à créer des compteurs anonymes.** Chaque objectif reçoit un nom humain dans un manifeste : `o1 = documents consultés`, `o2 = témoignage recueilli`, etc. Les sous-états ne servent que si le scoreboard d'étape ne suffit pas.

## IV.3 Convention des valeurs de progression

[NORME PROPOSÉE pour nouvelles quêtes ; incompatible avec une migration automatique des anciennes]

| Valeur | État |
|---:|---|
| `0` | Quête non commencée / non active |
| `10` | Acceptée / lancée, premier objectif ouvert |
| `20`, `30`, `40`… | Étapes réellement validées ; incréments de 10 laissent des espaces pour des correctifs |
| `90` à `99` | Prête à être remise, **uniquement si la quête distingue une remise** |
| `100` | Terminée définitivement, non répétable |

Ne jamais supposer que le JAR interprète intrinsèquement « 10 = commencée » ou « 100 = terminée » : ce sont **les plages déclarées dans `source`** qui définissent le fonctionnement du journal.

Exemple sans remise : `active min=10,max=99`, `completed min=100`.

Exemple avec remise : `active min=10,max=89`, `turn_in min=90,max=99`, `completed min=100`.

Échecs, interruptions, branches alternatives et répétitions doivent être documentés **quête par quête** avant d'adopter un code numérique supplémentaire. Si la remise ne s'applique pas, ne pas inventer un passage artificiel par `90`.

Une quête répétable ne doit pas se comporter comme une quête principale : documenter sa réinitialisation, son cooldown, ses récompenses et les champs `source.completed` réellement utilisés. Réutiliser le mécanisme existant du core si c'est lui qui est autoritaire.

## IV.4 Choix de dialogue et routeurs

[OBSERVÉ] Le datapack emploie deux **routeurs partagés** :

- `hcq_pnj` : ID numériques de déclenchement des PNJ EasyNPC, actuellement observés jusqu'à `40`.
- `hcq_choix` : ID numériques de choix ; plages observées `100–2599`, dont `1300–2599` réservées aux quêtes `J01–J13`.

**Règle impérative : ne jamais attribuer « 41 » à un nouveau PNJ ou `2600` à une nouvelle quête sans consulter le registre et le routeur de la version ACTUELLEMENT chargée.** Ce sont seulement des bornes constatées dans le snapshot v16.1.

[NORME PROPOSÉE]

- Conserver un registre `docs/quests/trigger_registry.csv` (l'annexe `Indivis_Registre_Triggers_Existants_v16_1.csv` en fournit l'état hérité observé) : `kind,id_min,id_max,owner,quest_code,pnj_key,description,status`.
- Réserver **un bloc non chevauchant par quête** pour les choix, ou un mécanisme de dispatch par contexte si le mod évolue ; ne jamais supposer une plage libre.
- Au clic, vérifier `@s` = joueur actif, l'étape, l'identité du PNJ et la distance autorisée.
- Réinitialiser le trigger après traitement **même en cas de choix invalide**, sans rejouer une ancienne action après reconnexion ou changement Nexus.
- Les questions facultatives restent **répétables** ; seule la commande « Continuer », « Conclure », « Remettre » fait avancer la quête.
- Ne jamais faire transiter un choix de Clément par `QuestChoix` du core sans contrat explicite : collisions et double-dispatch possibles.
- Ne pas déplacer un nombre de choix déjà publié : il peut être stocké dans des commandes EasyNPC existantes.

---

# V. Nomenclature des PNJ, tags, marqueurs et déclencheurs

## V.1 Quatre identifiants distincts pour un même PNJ

| Notion | Forme conseillée | Exemple |
|---|---|---|
| Clé unique stable du personnage | `pnj_key` | `archivaire_couronne` |
| Tag d'entité EasyNPC | `hcq_pnj_<pnj_key>` | `hcq_pnj_archivaire_couronne` |
| Cible dans un JSON de quête | `pnj:<pnj_key>` | `pnj:archivaire_couronne` |
| Tag de symbole de quête | `hcq_mrk_<pnj_key>` | `hcq_mrk_archivaire_couronne` |

Le **nom affiché** (« Archivaire de la Couronne ») est distinct de la clé. Il peut changer sans renommer le PNJ, son tag ou les quêtes qui le ciblent.

Ne jamais réutiliser la même clé pour deux PNJ différents ou créer une nouvelle clé à chaque quête lorsque le personnage est le même. Pour plusieurs gardes distincts, numéroter de manière stable (`sentinelle_archives_01`, `sentinelle_archives_02`).

[À AUDITER] Une cible `pnj:...` doit être enregistrée auprès du résolveur du JAR / des données de cibles ; **la présence d'un tag d'entité ne garantit pas, à elle seule, la résolution des waypoints `pnj:`**. Si le résolveur ne reconnaît pas une nouvelle clé, il faut l'intégrer conformément à son API, pas supposer une conversion automatique.

## V.2 Tags d'entité contre tags de données

Ne pas confondre :

1. Tag NBT d'entité au sens `/tag ... add hcq_pnj_*` : identifie une instance de PNJ.
2. Tag d'affichage `hcq_mrk_*` : identifie les entités `text_display` des symboles `!/?`.
3. Tag de visibilité `hcqvis.hcq_mrk_*.*` : mécanisme interne de visibilité conditionnelle.
4. `data/<namespace>/tags/.../*.json` : tag de **registre Minecraft** (fonctions, biomes, entités, etc.), qui n'est pas un score de joueur.
5. `tag:` dans `target` JSON : schéma de **résolution de cible**, non équivalent à la commande `/tag` sans résolution effective.
6. `minecraft:custom_data` des objets : métadonnées d'objets de quête, sans lien automatique avec les tags de visibilité.

**Politique :** ne pas utiliser des tags NBT de joueur seuls comme état persistant d'une quête, sauf prise en charge Nexus explicitement validée. Préférer le scoreboard de progression stocké dans le profil actif ; reconstruire les tags dérivés au chargement.

## V.3 Marqueurs `!` et `?`

[OBSERVÉ] Les fonctions `hc_quetes:marqueurs/<pnj>` calculent l'état pour chaque joueur :

- `0` : aucun marqueur ;
- `1` : `!` — quête disponible ;
- `2` : `?` — étape active à traiter / remise disponible.

Les marqueurs sont des `text_display` liés aux PNJ et possèdent des tags comme `hcq_mrk_hector`. Certaines fonctions sont **générées depuis le dialogue** et portent explicitement « Ne pas éditer ».

[NORME PROPOSÉE]

- Garder **un registre stable de `pnj_key`**, mais calculer l'état `!/?` à partir de l'état du **personnage Nexus actif**.
- Le `!` n'indique pas qu'une quête est déjà en cours ; le `?` indique qu'une interaction est pertinente pour sa progression.
- Si un PNJ distribue trois quêtes, centraliser la priorité des marqueurs plutôt que créer trois symboles superposés ; conserver les conventions du mod actuel.
- Les marqueurs d'une quête terminée doivent disparaître, sauf disponibilité d'une autre quête.
- **Ne pas éditer manuellement des fichiers générés** : modifier la source du dialogue / de la génération puis régénérer ; documenter le générateur ou, en son absence, arrêter et demander sa source.
- Un marqueur privé doit respecter la visibilité privée du PNJ et ne pas exposer une scène à un autre personnage Nexus.
- Contrôler l'impact performance en ville : aucune multiplication incontrôlée des `text_display`.

## V.4 PNJ permanents et PNJ scénarisés

- **Permanents partagés** : gardes, Scribe, Archivaire, Althéon. Leur présence est mondiale ; leurs **réponses et quêtes** sont personnelles.
- **Scénarisés individualisés** : messager de MQ01, apparitions et scènes privées. L'état d'instance doit être associé au **personnage Nexus**, jamais uniquement à l'UUID du compte.
- L'UUID Minecraft reste indispensable pour router un paquet réseau à la connexion courante ; ce n'est pas une clé suffisante pour la progression durable de plusieurs personnages d'un même compte.
- Si EasyNPC ne permet pas nativement la visibilité par ID de personnage Nexus, développer un pont explicite. Ne pas déclarer cette compatibilité comme acquise sans test.
- Ne pas faire voyager un PNJ mondial dans une scène que deux joueurs peuvent déclencher simultanément. Préférer une interaction immobile ou une animation client propre au destinataire.

---

# VI. Arborescence normalisée des nouvelles quêtes

[NORME PROPOSÉE — fichiers nouveaux uniquement]

```text
data/hc_quetes/
  hcq_quests/
    mq_01_convocation.json
    mq_02_registres.json
  function/
    quests/
      mq01/
        start.mcfunction
        interact.mcfunction
        choices.mcfunction
        progress.mcfunction
        complete.mcfunction
        sync.mcfunction
      mq02/
        ...
    pnj/
      ...                         (ponts d'interaction existants à prolonger)
    marqueurs/
      ...                         (chaîne existante à respecter)
    lib/
      ...                         (fonctions mutualisées sans effets annexes)
  advancement/
    interact/
      ...                         (déclencheurs si pertinents, nom stable)
```

Pour les fonctions de nouvelles quêtes : `hc_quetes:quests/<code>/<verbe>`.

| Fonction | Responsabilité unique |
|---|---|
| `start` | Vérifier les prérequis, démarrer une fois, sans récompenser deux fois |
| `interact` | Choisir le dialogue correspondant à l'état du personnage |
| `choices` | Valider les choix du routeur, sans collisions numériques |
| `progress` | Valider une transition d'étape et ses effets une seule fois |
| `complete` | Terminer, délivrer la récompense une seule fois, ouvrir la suite |
| `sync` | Recalculer affichages/états dérivés ; **aucune récompense ni transition** |

Les noms `start`, `interact`, `choices` sont **une convention proposée** et ne sont pas des fonctions déjà garanties par le JAR. Pour une quête comportant plusieurs scènes, créer `scene_01_*`, `scene_02_*` dans son sous-dossier au besoin. Réserver un vocabulaire cohérent (`accept`, `continue`, `turn_in`, `abort`) si plus adapté, et l'inscrire dans la convention plutôt que multiplier les synonymes sans raison.

### Ownership et pont core ↔ Clément

- **Core existant** : garde la maîtrise des quêtes `core_*` et de leurs scoreboards `QUEST_*`.
- **Nouvelles MQ du journal** : proposer `hc_quetes` comme propriétaire des états `hcq_mqXX` ; le core appelle des fonctions-ponts documentées pour les accès, le rang ou les événements monde.
- **Permissions `ACCESS_*`** : calculées par le gestionnaire d'accès du core, pas copiées dans un état indépendant du journal.
- **Réputation** : les scoreboards existants sont lus, jamais recréés à partir d'un type racial.
- **Compétences** : `CAP_POINTS_TOTAL` est **une variable proposée**, à alimenter uniquement par les gains effectifs de CapSkills et à sauvegarder par personnage Nexus ; ne pas la déduire du solde actuel de points.
- Toute communication entre namespaces doit avoir un appel unique documenté, un sens (qui écrit / qui lit) et un contrat d'idempotence.

---

# VII. Définition JSON normalisée du journal

## VII.1 Champs effectivement observés dans b6 / v16.1

| Champ | Statut recommandé | Règle |
|---|---|---|
| `id` | Obligatoire | Égale au nom du fichier, sans `.json` ; jamais renommé |
| `title` | Obligatoire | Intitulé narratif français ; ne pas préfixer inutilement par le code technique |
| `type` | Obligatoire | `MAIN`, `SIDE`, `COMBAT` ou `REPEATABLE` |
| `giver` | Obligatoire dans la norme | PNJ / autorité qui donne réellement la mission |
| `zone` | Obligatoire dans la norme | Nom RP visible, pas code interne ni coordonnées |
| `description` | Obligatoire | Situation de départ, enjeu et contexte ; ne pas divulgâcher |
| `can_abandon` | Obligatoire | `false` pour la grande intrigue, choix explicite ailleurs |
| `prerequisites` | Obligatoire dans la nouvelle norme | Tableau de vrais `id` de quêtes existantes, pas de titres |
| `rewards` | Obligatoire | Liste de textes honnêtes correspondant aux récompenses effectivement distribuées |
| `source` | Obligatoire | Définition du véritable scoreboard d'état et des plages |
| `objectives` | Obligatoire | Tableau ordonné des étapes avec ID locaux uniques |
| `next_quest` | Facultatif observé | ID réel de quête suivante ; vérifier dans le JAR si cela active automatiquement ou suggère seulement |

**Ne pas inventer de champ de JSON `arc_id`, `race_reputation`, `character_id`, `npc_uuid`, `waypoint_xaero`, `trigger_block`, `faction_gate` sans avoir vérifié sa prise en charge**. Ces informations appartiennent au registre de développement ou aux fonctions de condition, tant que le JAR b6 ne reconnaît pas un schéma étendu.

## VII.2 Source `SCOREBOARD`

Exemple valide sur le plan structurel :

```json
"source": {
  "kind": "SCOREBOARD",
  "active": {"objective":"hcq_mq01", "min":10, "max":99},
  "completed": {"objective":"hcq_mq01", "min":100}
}
```

Avec remise : utiliser `turn_in` et rendre non chevauchantes les plages active / prête à remettre. Les `abandon_commands` existent, mais ne doivent être ajoutées qu'avec **analyse de tous les effets**, en particulier lorsqu'un objet, une réputation ou un accès appartient à une autre quête. Les quêtes principales `can_abandon=false` ne doivent pas se voir ajouter une réinitialisation destructive par défaut.

## VII.3 Objectifs du journal

Champs reconnus dans les JSON existants : `id`, `type`, `text`, `done`, `target`, `waypoint`, et parfois `optional`, `done_item`, `when`, `amount`.

- `objectives[].id` : clé **locale à la quête**, stable ; forme `snake_case`, par exemple `presenter_convocation`, `voir_scribe`, `retour_archivaire`.
- `objectives[].done` : score réellement incrémenté par le système propriétaire ; ne pas créer une condition qui n'est jamais écrite.
- `objectives[].target` : cible utilisable par le type choisi, après enregistrement / contrôle.
- `optional:true` : uniquement pour un objectif réellement facultatif ; la quête reste terminable sans lui.
- `when` : condition d'affichage vérifiée sur la version du mod ; ne pas la substituer à la validation serveur.
- `done_item` : identifiant stable d'objet de quête correspondant aux métadonnées réelles ; ne pas se fier au nom affiché.
- `amount` : quantité numérique si pertinente (combat / collecte) ; respecter les limitations du type.

### Exemple de squelette MQ01

Le fichier compagnon `examples/mq_01_convocation.json` montre **la structure attendue**, mais n'implémente pas la quête : il manque encore les fonctions serveur, le registre de cibles et les coordonnées réelles. Il ne doit **pas** être installé seul.

## VII.4 Cibles et waypoints Xaero

[OBSERVÉ] Le JSON utilise `waypoint.mode` avec `NONE`, `ZONE` et `EXACT` et des cibles préfixées par `pnj:`, `lieu:`, `zone:`, `tag:`, `spawn:` ou `xyz:`.

[NORME PROPOSÉE]

| Situation | `waypoint.mode` | Exemple / précaution |
|---|---|---|
| Destination identifiable et connue | `EXACT` | `target:"pnj:archivaire_couronne"` **si résolveur configuré** |
| Région générale connue | `ZONE` | `target:"zone:havre_fort_port"` **si zone enregistrée** |
| Destination encore inconnue | `NONE` | La quête décrit l'indice, sans révéler les coordonnées |

- `waypoint.target` doit référencer une clé **réellement existante dans le registre des cibles** ; ne pas inventer un nom comme s'il était déjà résolu.
- `waypoint.label` doit être bref, cohérent avec les connaissances du personnage et différent du titre complet de la quête si nécessaire.
- La position exacte n'est fournie que si le niveau de connaissance narratif l'autorise. La fin de MQ03 **oriente vers le port de Havre-Fort**, jamais vers l'île encore inconnue.
- Le waypoint du suivi de quête **et** les marqueurs `!/?` sont **deux systèmes distincts à tester**.
- Après changement de personnage Nexus, le suivi de quête et le waypoint doivent être actualisés ; ne pas réutiliser le dernier waypoint d'un personnage précédent.
- Le pont Xaero dépend des composants réellement installés ; l'existence d'un JSON `waypoint` n'atteste pas que le repère apparaît effectivement sur le client.
- Les changements d'objectif doivent avoir un effet visible rapide sans recréer des waypoints à chaque tick.

---

# VIII. Objets, tags d'items, événements et repères spatiaux

## VIII.1 Objets de quête

[OBSERVÉ] De nombreux objets portent des données sous la forme `minecraft:custom_data` avec la clé `hc_quete`, par exemple une valeur `j01_harnais`.

[NORME PROPOSÉE pour nouveaux objets]

`hc_quete : "<code>_<objet>"`

Exemples : `mq01_convocation`, `mq03_notes_route`, `sqhf001_carte`.

- Le code est stable ; l'objet ne change pas d'identité si son nom affiché, son icône ou son texte évolue.
- Éviter les critères uniquement fondés sur le nom de l'objet (`custom_name`) ou son lore.
- Documenter les conditions de remise, perte, duplication et récupération.
- Ne pas utiliser un objet non récupérable comme **unique source de vérité** d'une quête principale : l'état serveur fait foi.
- La lettre de MQ01 peut employer un modèle d'icône de feuillet personnalisé ; cette ressource graphique est distincte de l'identifiant `hc_quete`.

## VIII.2 Repères et zones

- Clé logique de lieu, stable : `lieu:<slug>` après déclaration dans le système de cibles.
- Zone géographique : `zone:<slug>` ; préciser dimension, périmètre ou tag de biome validé.
- Éviter les coordonnées inventées et les hypothèses sur la carte.
- Pour les nouvelles données de biomes, ne pas confondre `indivis:city` avec « ville impériale » : le tag existant inclut aussi des villes étrangères. Prévoir un **tag ou une liste explicitement impériale** pour les apparitions du messager.
- Toute safezone, musique de ville ou zone de spawn doit être vérifiée dans le monde chargé. Réutiliser les zonages existants seulement s'ils ont exactement la bonne sémantique.

## VIII.3 Événements du monde et interfaces

Décrire séparément le **déclencheur** (dialogue, mort de mob, entrée de zone, clic sur bloc, action EasyNPC), le **filtre** (personnage, quête, étape, zone) et l'**effet unique** (progression, item, réputation, accès). Aucun handler de tick ne doit distribuer plusieurs fois la même récompense lorsque sa condition reste vraie.

---

# IX. Personnages Nexus : norme de persistance et de synchronisation

## IX.1 Deux identités, deux fonctions

- UUID du compte Minecraft : connexion, session réseau, destinataire actuellement en ligne.
- Identifiant stable du personnage Nexus : **véritable propriétaire de la progression RP**.

**Interdit :** sauvegarde narrative permanente sous la seule clé UUID du compte, si plusieurs personnages peuvent être sélectionnés sur ce compte.

**Obligatoire pour nouvelles quêtes :** la progression doit être sauvegardée/restaurée dans l'instantané Nexus du personnage actif et les caches d'affichage doivent être invalidés lors du changement de personnage.

[OBSERVÉ dans le core RC9BC] `data/capitale/profile_persistence/policy.json` décrit des objectifs à conserver selon les préfixes `REP_`, `QUEST_`, `ACCESS_`, `GUILD_`, `JOB_`, `FACTION_`, `CAPSK_`, `CAP_`, avec exclusions. **Ce fichier de politique seul ne garantit pas la sauvegarde de `hcq_*` ou `hcc_*`**.

[CONTEXTE DU PROJET, À VÉRIFIER DANS LA VERSION ACTIVE] La lignée Nexus PRE33 QUESTSYNC puis SYNC2/PRE34 a été développée pour corriger la persistance des objectifs de Clément. Clément ne doit néanmoins jamais présumer que **tous les nouveaux préfixes** sont automatiquement sauvegardés : vérifier la politique effective et un cycle réel A → B → A.

## IX.2 Registre des champs persistants

Le registre technique doit désigner pour chaque scoreboard : `name,owner,scope,persistence,reset_policy,derived,first_release`.

Valeurs de `scope` recommandées :

- `character` : progression, choix, réputation et autorisation personnelle.
- `session` : menu de dialogue ouvert, temporisation de clic, verrou éphémère ; **à nettoyer** au changement.
- `world` : événements véritablement mondiaux, indépendants de la volonté d'un personnage ; pas de faux états mondiaux pour une quête personnelle.

`derived=true` signifie : recalculable à partir d'une autre source autoritaire (ex. tag d'accès effectif, disponibilité d'un marqueur). **Un état dérivé ne doit pas être traité comme une deuxième vérité durable.**

## IX.3 Séquence minimale de changement de personnage

1. Bloquer / clôturer les interactions encore actives pour le personnage A.
2. Persister atomiquement les scores et données du personnage A.
3. Nettoyer les caches et états de session susceptibles d'être hérités.
4. Charger l'identité et les états du personnage B.
5. Recalculer les **droits effectifs** `ACCESS_*` à partir de leurs sources.
6. Actualiser la disponibilité des quêtes, l'état du messager et les cibles de dialogue.
7. Recalculer les marqueurs `!/?`, la visibilité des entités et l'objectif suivi dans le journal.
8. Resynchroniser le waypoint Xaero vers le client courant.
9. **Ne jamais** débloquer de quête, payer une récompense ou modifier un choix narratif pendant une restauration.

Il faudra faire valider **l'ordre exact** des hooks par le code Nexus en production. Cette liste décrit le contrat fonctionnel, pas une API Nexus déjà identifiée.

## IX.4 Scènes privées et messager

Le messager de MQ01 doit être réservé au personnage ayant : prologue achevé, rang au moins Résident, `CAP_POINTS_TOTAL >= 5` (seuil provisoire), MQ01 pas encore démarrée. La citoyenneté n'est pas requise.

- `CAP_POINTS_TOTAL` = **somme historique des points gagnés**, jamais leur solde disponible, jamais une somme reconstruite des points dépensés.
- Si le personnage remplit la condition hors d'une ville impériale éligible, enregistrer l'événement « convocation en attente ».
- Apparition dans une zone habitée impériale sûre, non dans une cité étrangère simplement parce qu'elle porte le tag `indivis:city`.
- Le messager ne doit pas apparaître à l'autre personnage du même compte, ni à un autre joueur.
- Interruption, déconnexion, mort, changement de personnage et inventaire plein ne doivent pas bloquer la convocation ni dupliquer la lettre.

---

# X. Accès et gardes : priorité 3, sources et compatibilité

## X.1 Valeurs existantes et extension

[OBSERVÉ dans le core RC9BC]

- `ACCESS_* = 0` : aucun accès.
- `ACCESS_* = 1` : autorisation accordée individuellement.
- `ACCESS_* = 2` : autorisation recalculée selon le rang social, **non chronométrée en tant que telle**.
- `CAP_PASS_ZONE`, `CAP_PASS_TIMER` et le contexte des gardes sont des mécanismes distincts pour les laissez-passer et reconduites chronométrés.

[NORME PROPOSÉE] `ACCESS_* = 3` : accès effectif **prioritaire**, accordé par une quête pour une période narrative déterminée, même longue.

### Règle essentielle : `3` est la valeur effective, pas l'unique historique des droits

Conserver des sources indépendantes : autorisations de rang, autorisations manuelles et quêtes actives. Le score affiché `ACCESS_ARCHIVES=3` ne suffit pas à dire quelle quête a accordé ce droit.

Lors de la révocation, supprimer **uniquement la source de la quête arrivée à terme** puis recalculer. Cas tests obligatoires :

- `0 -> 3 -> 0` ;
- `2 -> 3 -> 2` ;
- `1 -> 3 -> 1` ;
- deux quêtes donnent accès simultanément : `3` reste actif tant qu'il reste une source ;
- un accès permanent acquis **pendant** une quête doit survivre à la fin de celle-ci ;
- recalcul de rang, reconnexion et passage A/B/A entre personnages Nexus.

La priorité exigée est **3 au-dessus de tous les droits ordinaires**. La résolution relative `1` contre `2` doit conserver la politique déjà en vigueur ou être arbitrée avec les développeurs du core, sans changement silencieux.

[À AUDITER] `access/sync_tags_self` peut reconstruire une valeur `1` à partir d'un tag `ACCESS_*` conservé avec score zéro. La migration doit corriger l'ordre d'autorité pour éviter qu'une révocation ressuscite un accès. Ne pas se contenter de modifier un seul fichier d'attribution.

## X.2 Dialogues et timer de reconduite

- Criminalité **active** : traitement judiciaire prioritaire.
- Réputation institutionnelle : module l'attitude personnelle.
- Race : variante culturelle pertinente, y compris avec bonne ou mauvaise réputation ; ne jamais inventer la profession, l'exploit ou l'origine personnelle du personnage.
- Questions facultatives : répétables, sans effets persistants.
- Gardes : suspendre le timer de reconduite pendant une conversation valide ; après autorisation, annuler toute téléportation différée. Après refus, restaurer le comportement normal.
- Une autorisation d'accès aux Archives ne vaut pas accès à leurs sections classifiées.

Pour MQ02 : accès de quête à la présentation aux Sentinelles, valable pendant l'entretien, retrait **après départ effectif** des Archives. Pour MQ03 : le Scribe peut avertir les gardes du retour du personnage ; nouvelle autorisation limitée, puis même logique de retrait. Préserver les autres droits.

---

# XI. Processus de création d'une nouvelle quête — obligatoire pour Clément

## Étape 1. Déclarer avant d'écrire

Ajouter au registre : ID canonique, code interne, type, zone, arc, propriétaire de progression, scoreboard principal, prérequis, PNJ et cibles prévus. Réserver les plages numériques de `hcq_choix` / `hcq_pnj` dans le registre des triggers.

## Étape 2. Définir un contrat fonctionnel sur une page

Décrire : déclencheur, fin, état par étape, conditions, objectifs optionnels, branches, récompense, abandon éventuel, droits d'accès, répétition et cas multijoueur. Faire valider ce contrat narratif avant le développement.

## Étape 3. Implémenter une seule source d'état

Créer l'objectif `hcq_<code>` et les rares sous-scores utiles ; faire écrire ses transitions par un seul ensemble de fonctions. Adapter les listeners core/EasyNPC pour appeler ces fonctions au bon joueur, sans duplication du traitement.

## Étape 4. Ajouter le JSON journal

ID = nom du fichier ; `source` = scoreboard réel ; `done` = état effectivement émis ; cibles résolubles ; textes et récompenses exacts ; `prerequisites` existants. Vérifier le JSON dans le JAR installé.

## Étape 5. Brancher les PNJ et les marqueurs

Tags PNJ uniques ; routeurs numériques réservés ; `!/?` cohérents avec l'étape ; questions facultatives répétables ; aucune duplication de PNJ permanents. Utiliser les sources/générateurs des marqueurs, pas l'édition manuelle de sorties générées.

## Étape 6. Brancher le waypoint

Tester la quête suivie, le `target` et le waypoint Xaero réel. Pour un lieu inconnu : `NONE` ou zone volontairement approximative. Ne pas communiquer le secret au client trop tôt.

## Étape 7. Vérifier Nexus et l'environnement multijoueur

Deux comptes simultanés ; deux personnages successifs du **même** compte ; reconnexion ; mort ; retour de sauvegarde. Vérifier scores, marqueurs, visibilités, inventaires, livres, réputation, accès, états de dialogue et waypoint.

## Étape 8. Déployer sans casser le serveur

Copie de test → audit statique → serveur Fabric de test → diff exhaustif → plan de retour arrière → validation humaine → production. **Jamais remplacer aveuglément le core ou la v16.1 du datapack de Clément.**

---

# XII. Contrôles automatiques à imposer en revue de code

Tout ajout doit échouer au contrôle s'il contient :

1. Un `id` de quête déjà existant ou un nom de fichier différent de l'`id` déclaré.
2. Un nouvel ID hors des formes `mq_`, `sq_`, `cq_`, `rq_` sans exception acceptée.
3. Un code court ou scoreboard déjà attribué à une autre quête.
4. Une valeur `type` inconnue du JAR b6.
5. Un `prerequisites` / `next_quest` pointant vers un ID inexistant, hors exception documentée pour contenus futurs non livrés.
6. Un objectif local dupliqué, un `done.objective` non initialisé ou une plage active / terminée incohérente.
7. Une cible de waypoint impossible à résoudre ou une coordonnée provisoire déployée comme définitive.
8. Un `hcq_pnj` numérique ou bloc `hcq_choix` empiétant sur une réservation existante.
9. Une commande de récompense / de fin de quête dans un simple `sync` ou un affichage `journal`.
10. Un objet de quête défini uniquement par son nom affiché.
11. Un tag persistant de joueur non couvert par la sauvegarde par personnage Nexus.
12. Une autorisation `ACCESS_* = 3` qui efface un accès permanent préexistant.
13. Une règle qui présume la citoyenneté comme prérequis de MQ01.
14. Un accès ou waypoint d'un personnage visible sur son autre personnage Nexus.
15. Un écart non justifié entre les ressources déclarées (marqueur, PNJ, journal) et les fonctions effectivement livrées.

Les fichiers historiques ne doivent pas « échouer » uniquement parce qu'ils ne suivent pas la **nouvelle** nomenclature : appliquer une vérification des différences sur les fichiers ajoutés/modifiés, puis traiter la dette technique dans une migration spécifique.

---

# XIII. Matrice de tests minimale (développeur / QA)

| Test | Résultat requis |
|---|---|
| Nouveau personnage A à 4 points | MQ01 indisponible |
| Passage au cinquième point (total historique) | Convocation en attente / disponible au bon endroit, une seule fois |
| Dépense de points | `CAP_POINTS_TOTAL` inchangé |
| Résident non citoyen | MQ01 accessible ; les quêtes réellement citoyennes restent réservées |
| Ville `indivis:city` étrangère | Aucun messager de la Couronne non justifié |
| Messager refusé / déconnexion | Reprise possible, aucune duplication |
| Sentinelle réputation + / neutre / - | Variantes raciales, accès si convocation valide |
| Criminalité active | Traitement judiciaire sans passe-droit |
| Questions facultatives répétées | Aucune nouvelle progression ni double récompense |
| Dialogue garde + timer | Pas de reconduite pendant un entretien valide ; pas de timer fantôme |
| Accès de quête 3 sur 0/1/2 | Les droits 0/1/2 reviennent correctement après clôture |
| Deux quêtes donnent le même accès | Une clôture n'enlève pas l'autre |
| Deux joueurs parlent au même Archivaire | États et dialogues indépendants ; aucun mouvement mondial incohérent |
| Deux personnages A/B du même compte | Isolation intégrale des quêtes, réputation, points, accès et marquages |
| Retour en A après B | Restauration exacte, sans rétribution ni nouvelle validation |
| `!/?` au-dessus des PNJ | Visible seulement quand pertinent pour le personnage |
| Waypoint Xaero de suivi | Destination pertinente, pas d'héritage d'un autre personnage |
| Fin MQ03 | MQ04 sélectionnée ; waypoint **Havre-Fort port**, pas l'île |
| Anciennes quêtes b21/v16.1 | Progression, récompenses, marqueurs et livres inchangés |
| Profil ancien et migration Nexus | Pas de fuite d'état ; conservation des sauvegardes et procédure de restauration |

**Rapport de tests requis** : pour chaque ligne, préciser `statiquement vérifié`, `exécuté avec banc de doubles`, `testé en serveur Fabric réel` ou `non testé`. Les trois statuts ne sont pas interchangeables.

---

# XIV. Exemples précis de raccordement MQ01–MQ04

**Ces exemples illustrent la convention : ils ne valent pas ordre d'implémenter les scènes sans le cahier des charges narratif validé.**

| Quête | Code | Début | Dernière étape | Source d'état | Marqueur / waypoint |
|---|---|---|---|---|---|
| MQ01 Convocation | `mq01` | Dialogue final du messager (après rang Résident et 5 points) | Convocation vérifiée aux Sentinelles | `hcq_mq01` | Messager `!/?` seulement si scène individuelle ; puis Archives |
| MQ02 Registres | `mq02` | Validation par les Sentinelles | Entretien avec l'Archivaire terminé | `hcq_mq02` | Scribe → Archivaire ; accès Archives = 3 durant visite |
| MQ03 Journal de bord | `mq03` | Départ à la recherche d'Althéon | Retour à l'Archivaire et analyse de carte | `hcq_mq03` | Althéon → Archives ; second accès = 3 |
| MQ04 Havre-Fort | `mq04` | Dialogue final de MQ03 | **À définir** | `hcq_mq04` | Port de Havre-Fort ; waypoint Xaero et journal |

La première quête principale doit être **déclenchée sans citoyenneté impériale obligatoire**. Le messager ne choisit pas sa destination par hasard : utiliser les zones habitées impériales et safezones effectivement présentes et filtrer les villes étrangères.

**Attention au récit** : les informations sur les Anciens, le monolithe du sauvetage et l'anomalie d'ancrage ne doivent pas apparaître avant leur révélation scénaristique. Les documents du capitaine donnent seulement une **zone de recherche approximative**. L'Archivaire est récurrent, réservé et curieux ; les Scribes n'ont pas accès d'office à des secrets de haut niveau.

---

# XV. Décisions nécessitant encore validation avant développement

1. **Adopter officiellement les nouveaux préfixes** `mq_`, `sq_`, `cq_`, `rq_` et les codes géographiques du registre.
2. **Attribuer les plages numériques** `hcq_pnj` et `hcq_choix` à partir de la **version actuellement installée**, et versionner leur inventaire.
3. **Valider l'API réelle du résolveur** pour les nouveaux `pnj:`, `lieu:`, `zone:` et coordonnées exactes de Havre-Fort.
4. **Déterminer l'identifiant d'un personnage Nexus** et l'ordre réel des hooks de restauration sur PRE34 (ou version plus récente).
5. **Valider la réalisation d'une visibilité privée** des PNJ EasyNPC par personnage Nexus, et non seulement par UUID Minecraft.
6. **Arbitrer la source d'autorité des accès 1 contre 2**, tout en garantissant **3 prioritaire**.
7. **Fixer après étude de CapSkills** si le seuil provisoire de cinq points doit évoluer et comment `CAP_POINTS_TOTAL` est alimenté.
8. **Prendre comme base les versions effectives du serveur**, et non considérer RC9BC/b21 comme les derniers fichiers.

---

# Annexes

## A. Précautions sur les migrations

L'ancien ID d'une quête est une donnée de sauvegarde. Une migration éventuelle `mc_*` → `sq_*` ou `QUEST_*` → `hcq_*` exige un tableau de correspondance, des règles de copie des états, une compatibilité temporaire des prérequis et un script réversible testé avec des profils existants. **Ce n'est pas demandé ici.**

Ne pas modifier les 66 quêtes simplement pour faire passer un validateur de style. La normalisation s'applique **aux ajouts** et les changements des anciennes quêtes doivent être limités à des corrections ciblées.

## B. Livrables minimaux exigés pour toute future quête

- Entrée dans le registre des ID et codes courts.
- Contrat narratif validé, état par étape, conditions/récompenses.
- JSON de quête conforme au JAR cible.
- Fonctions d'interaction et d'avancement documentées.
- Tags PNJ, plages numériques et résolutions de cibles référencés.
- Conditions de marqueurs `!/?` et de waypoint Xaero.
- Conformité à la sauvegarde par personnage Nexus.
- Tests solo, multijoueur et changement de personnage.
- Diff exact, consignes d'installation et plan de retour arrière.

## C. Archives de référence et obsolescence

Contrôles réalisés sur **core RC9BC**, **hc_quetes b21 lore v16.1** et **mod b6**. Le serveur peut utiliser **core 1.5.7 et Nexus PRE34 ou ultérieur**. **Tout résultat de ce guide qui décrit le code existant doit être revérifié sur la version active** avant modification et compilation.

---

*Fin de la norme 1.0 — document de spécification, non datapack ni certification de fonctionnement réel.*

## D. Validation automatique fournie

Le paquet contient un utilitaire **facultatif** `tools/Indivis_Controle_Nomenclature_Quetes.py` (Python standard, sans dépendances), à exécuter depuis un environnement de développement :

```bash
python tools/Indivis_Controle_Nomenclature_Quetes.py hc_quetes.zip
python tools/Indivis_Controle_Nomenclature_Quetes.py examples/mq_01_convocation.json
```

Il contrôle notamment : unicité des ID au sein du pack, forme des nouvelles familles, concordance nom/ID de fichier, types et champs essentiels, cible syntaxique et nom du scoreboard principal. **Il ne vérifie pas tous les liens entre scoreboards, PNJ et Java, ni les véritables interactions en jeu.** Le validateur admet les 66 quêtes héritées sans imposer leur renommage.

Sur le snapshot v16.1, son contrôle relève **0 erreur et 6 avertissements** : six quêtes répétables héritées utilisent un scoreboard distinct pour le statut terminé, ce qui peut être correct avec le système de cooldown. Ne pas « corriger » ces avertissements sans analyser leur code.
