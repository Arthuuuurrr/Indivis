# Haute Capitale — Métiers

Système de métiers MMO pour le serveur Haute Capitale.
**Minecraft Java · Fabric 1.21.11 · Java 21**

Mod ID : `haute_capitale_metiers`

---

## État : livré — 0.1.0.b12, les onze étapes validées en jeu, puis l'Alchimiste

Déclaré fini le 13 septembre 2026, après essais sur le pack complet (serveur
d'essai et client pack, 122 mods). Le jar, Farmer's Delight et FleshZ sont
dans le dossier `mods` du joueur ; rien n'a été installé sur le serveur, c'est
lui qui le fait. Toute modification ultérieure prend un nouveau numéro (b8…).

Ce qui existe aujourd'hui :

- **Étape 1, le socle.** Les dix métiers, leur progression 1 à 50, la persistance
  des données joueur et les commandes d'administration.
- **Étape 2, le moteur de données.** Le contenu du MMO vit dans des fichiers de
  datapack rechargeables à chaud, jamais dans le code Java. Validation à la
  lecture, messages d'erreur qui nomment le fichier et la ligne, inspection.
- **Étape 3, le pont Easy NPC.** Un clic droit sur un Easy NPC ouvre son
  interface de métier. Une seule architecture pour les dix-sept rôles, aucun mixin,
  aucune nouvelle entité, aucun villageois.
- **Étape 4, les matières.** Les dix matières du catalogue existent, les dix
  tags de compatibilité aussi, et FleshZ tourne en 1.21.11 sans doubler nos
  futures carcasses.
- **Étape 5, les recettes.** Fabriquer chez un PNJ, gagner une XP qui décroît
  quand la recette devient trop facile, et voir une recette devenir maîtrisée.
  Métier pilote : le Travailleur du cuir et sa chaîne de tannage.
- **Étape 6, le moteur de nodes.** Poser un filon ou une plante, la récolter,
  la voir repousser — sans que personne, jamais, ne puisse creuser la carte.
  Quatorze types livrés pour le Mineur et l'Herboriste, sur des blocs vanilla.
- **Étape 7, le Chasseur.** Tuer une proie déclarée donne de l'XP — sauf si elle
  vient d'un élevage, d'un générateur, d'un œuf ou d'une commande. L'origine
  est marquée à l'apparition et survit au redémarrage. Trente proies et les
  Martins des orcs, en fichiers.
- **Étape 8, les carcasses.** Une proie abattue laisse une carcasse qui lui
  ressemble — la créature elle-même, couchée, reconstruite côté client — qu'un
  seul Dépeceur travaille, couteau en main, en restant en place. La boucle est
  complète : Chasseur → carcasse → Dépeceur → Travailleur du cuir.
- **Étape 9, le Forgeron.** Douze recettes — les composants d'Epic Knights et
  les dagues d'Icaria, couteaux du Dépeceur —, et la **réparation universelle** :
  tout objet à durabilité, de tout mod, contre des Martins, pour tout joueur ;
  et les objets se brisent au lieu de disparaître.
- **Étape 10, l'Ingénieur.** Trente et un gadgets — trente recettes par palier,
  de 6 à 45 — réécrits en Java dans notre espace de noms à partir des idées de
  MBK's Useful Items (CC0) et de Gadgets Against Grind (MIT), icônes redessinées
  en 16×16 ; et la **Pierre de foyer**, liée chez l'aubergiste, qui ramène à
  l'auberge après une canalisation. Aucun gadget ne casse ni ne pose un bloc.
- **Étape 11, les trois derniers artisans.** Le **Couturier** et sa chaîne
  textile (coton → fibre → bobine → tissu → composants → vêtement, quarante
  objets, un node de cotonnier), le **Joaillier** — que des fichiers, ses cent
  dix bijoux existaient —, le **Cuisinier** et ses soixante-huit plats, chacun
  porteur d'un **buff de repas** (un seul à la fois, remplacé par le suivant,
  retiré à la mort) ; et, communes à tous les artisans, la **qualité
  Excellent** et la marmite de Farmer's Delight bridée aux plats simples.

- **Après les étapes, l'Alchimiste.** Un onzième métier, demandé à l'essai :
  il fabrique les **potions du jeu** — à boire, jetables, persistantes — à
  partir des plantes de l'Herboriste, qui devient son fournisseur avec vingt
  herbes de Lands of Icaria et neuf plantes sauvages de Farmer's Delight.

Les onze métiers sont jouables de 1 à 50.

## Les onze métiers

| Récolte | Artisanat |
|---|---|
| Mineur | Forgeron |
| Herboriste | Couturier |
| Chasseur | Travailleur du cuir |
| Dépeceur | Joaillier |
| | Cuisinier |
| | Ingénieur |
| | Alchimiste |

Chaque métier a sa propre progression, totalement indépendante des autres et du
niveau de combat du joueur. Rangs : Apprenti 1-10, Compagnon 11-20, Expert
21-30, Maître 31-40, Grand Maître 41-50.

## Configuration

`config/haute_capitale_metiers.json`, créé au premier démarrage.

```json
{
  "niveau_max": 50,
  "metiers_max_par_joueur": 0,
  "xp_base": 75.0,
  "xp_exposant": 1.1,
  "decote": [
    { "ecart": 5,  "pourcent": 100 },
    { "ecart": 10, "pourcent": 60 },
    { "ecart": 15, "pourcent": 25 },
    { "ecart": 20, "pourcent": 5 }
  ],
  "maitrise_apprentissage": 5,
  "maitrise_acquise": 15,
  "monnaie": "capitale_currency:martin_dor",
  "proteger_les_blocs": true,
  "nodes_balayage_secondes": 5,
  "origine_par_defaut": "naturelle",
  "depecage_portee": 3.0,
  "depecage_outil": "haute_capitale_metiers:couteaux",
  "carcasses_max_par_zone": 8,
  "reparation": {
    "commun": 0.05,
    "peu_commun": 0.08,
    "rare": 0.12,
    "epique": 0.2,
    "legendaire": 0.35,
    "brise": 2.5,
    "minimum": 1,
    "exceptions": []
  }
}
```

L'XP pour passer du niveau N au suivant vaut `xp_base × N^xp_exposant`.
Avec les valeurs par défaut : 75 XP du niveau 1 au 2, 5 423 du 49 au 50, et
**129 238 XP au total** pour atteindre le niveau 50.

`metiers_max_par_joueur` à 0 signifie « aucune limite ».

**`decote`** — la part d'XP accordée selon l'écart *niveau du joueur − niveau de
la recette*. Chaque palier dit « jusqu'à cet écart, ce pourcentage » ; au-delà
du dernier, 0 %. Une recette de niveau 1 rapporte donc 100 % à un artisan de
niveau 6, 60 % à un niveau 10, 25 % à un niveau 13, rien à un niveau 22 — mais
se fabrique toujours. C'est ce qui empêche de monter un métier sur des objets
triviaux. La même décote servira aux nodes et aux proies.

**`maitrise_*`** — après `maitrise_apprentissage` fabrications une recette est
« en apprentissage » (jauge, remonte dans la liste) ; après `maitrise_acquise`
elle est maîtrisée (✦, fabrication ×5 et ×10). La maîtrise ne donne jamais d'XP
et ne change rien à l'objet : c'est du confort.

**`monnaie`** — l'objet qui paie les recettes. Si le mod qui le fournit est
absent, les recettes payantes sont proposées mais refusées avec un message
clair ; les gratuites fonctionnent.

**`proteger_les_blocs`** — en survie, aucun bloc ne se casse, node ou pas.
Le mode créatif n'est pas concerné. Un node, lui, ne se casse jamais, quel que
soit le mode : il se retire avec la baguette ou la commande.

**`nodes_balayage_secondes`** — toutes les combien de secondes le serveur
regarde si des nodes récoltés ont repoussé. Le balayage ne parcourt que les
nodes en attente dont le chunk est chargé : quelques entrées, jamais la carte.

**`origine_par_defaut`** — l'origine attribuée à une créature adulte rencontrée
sans marque : la faune d'avant l'installation du mod, ou un mob créé par un
mod sans passer par l'initialisation de Minecraft. `naturelle` pour ne pas
priver les chasseurs d'un monde déjà peuplé ; `inconnue` pour être strict.

**`depecage_portee`** — distance maximale, en blocs, entre le Dépeceur et la
carcasse. **`depecage_outil`** — la famille d'outils exigée quand la fiche n'en
nomme pas ; vide = aucun outil. **`carcasses_max_par_zone`** — au-delà, dans un
rayon de seize blocs, la plus ancienne carcasse disparaît.

**`reparation`** — le barème de la réparation universelle, en Martins par point
de durabilité manquant, par rareté ; `brise`, le multiplicateur d'un objet
brisé ; `minimum`, le prix plancher ; `exceptions`, les objets qui doivent
continuer à disparaître à zéro parce que leur mod gère lui-même leur
destruction — voir Le Forgeron.

**`gadgets`** — les rayons et durées des gadgets de l'Ingénieur : `aimant_rayon`
(6), `scanner_rayon` (12), `geode_rayon` (48), `echo_rayon` (96),
`resonateur_rayon` (10), `tresor_rayon` (12), `grappin_portee` (24),
`obscurite` (niveau de lumière en dessous duquel casque et lanterne
s'allument, 5), `raffinage_secondes` (60), `campement_secondes` (30) et sa
recharge (300), `secours_recharge_secondes` (120), la balise de rappel
(`rappel_canalisation_secondes` 4, `rappel_recharge_secondes` 180,
`rappel_portee` 1500), `evasion_recharge_secondes` (60), les trois repoussants
(`sel` 5 min / 8 blocs, `onguent` 15 min / 16, `baume` 60 min / 24), le drone
(`drone_portee` 24, `drone_ramassage` 3) et `exosquelette_bonus`.

**`foyer`** — la Pierre de foyer : `canalisation_secondes` (5),
`recharge_secondes` (900) et `amelioree_recharge_secondes` (300), `portee`
(4000 blocs, 0 = illimitée) et `amelioree_portee_facteur` (× 2),
`autre_dimension` (false : un foyer dans un autre monde est hors de portée),
`pierre_offerte` (true : l'aubergiste donne une pierre au joueur qui n'en a
pas) et `auberges_max` (0 : sans limite — sinon, au-delà, la plus ancienne
auberge est oubliée).

**`qualite`** — la chance qu'un objet fabriqué soit Excellent : rien en
dessous de `ecart_minimum` niveaux au-dessus de la recette (3), puis
`chance_base` (5 %) plus `chance_par_niveau` (1,5 %) par niveau d'écart
supplémentaire, jusqu'à `chance_max` (35 %). Un Grand Maître qui fabrique une
recette de niveau 20 la réussit Excellent une fois sur trois.

**`repas`** — les buffs de repas : `excellent_facteur` (4/3 : un plat
Excellent renforce son buff d'un tiers) et `duree_secondes` (900, quand la
fiche de buff n'en donne pas).

Un fichier de configuration écrit par une version précédente est complété au
chargement : les réglages nouveaux y apparaissent avec leur valeur par défaut.

Toute modification se recharge en jeu avec `/metiers recharger`, sans
redémarrage.

## Le contenu vit dans les datapacks

Rien de ce qui décrit le monde n'est écrit en Java. Une créature est chassable
parce que son fichier le dit, pas parce qu'une condition la nomme quelque part.

Un fichier se place dans n'importe quel datapack — celui du mod, un datapack de
monde, un datapack de serveur — sous :

```
data/<namespace>/hcm/<domaine>/<chemin>.json
```

**Le chemin est la clé.** `data/cubeanimals/hcm/creatures/roedeer.json` décrit
`cubeanimals:roedeer`. C'est la convention de Minecraft pour les tables de butin
et les recettes : les doublons deviennent impossibles et l'identifiant n'est pas
répété dans le fichier. Un fichier placé dans un sous-dossier garde le
sous-dossier dans sa clé.

Un domaine par système. Aujourd'hui : `creatures`, `roles`, `recipes` et `nodes`.

### Fiche de créature

Tous les champs sont optionnels sauf `matiere` à l'intérieur de `depecage`. Une
fiche sans `chasseur` décrit une créature qu'on ne chasse pas ; une fiche sans
`depecage` décrit une créature qu'on ne dépèce pas.

```json
{
  "commentaire": "libre, affiché par la commande d'inspection",
  "categorie": "animal",
  "rarete": "peu_commune",

  "chasseur": { "niveau": 4, "xp": 12.0 },

  "depecage": {
    "niveau": 6,
    "xp": 18.0,
    "duree_carcasse": 90,
    "matiere":     { "id": "minecraft:leather", "min": 1, "max": 3 },
    "secondaires": [ { "id": "minecraft:bone", "chance": 0.3 } ]
  },

  "viande":      { "id": "minecraft:mutton", "min": 1, "max": 2 },
  "loot_combat": [ { "id": "minecraft:string", "chance": 0.15 } ],

  "origines_xp": ["naturelle", "spawn_mmo"]
}
```

| Champ | Défaut | Rôle |
|---|---|---|
| `categorie` | `autre` | Famille, pour le tri et les filtres. Ne décide jamais seule de rien. |
| `rarete` | `commune` | Guide la valeur du butin. |
| `chasseur` | absent | Niveau requis et XP gagnée par le Chasseur — voir Le Chasseur. |
| `depecage` | absent | Niveau requis, XP, durée de la carcasse, canalisation, outil, matières du Dépeceur — voir Les carcasses. |
| `viande` | absent | Ce qu'elle laisse à manger quand un joueur la tue, quelle que soit son origine. |
| `loot_combat` | vide | Butin de combat, tombé au sol à la mort si l'origine est éligible — les Martins des orcs. |
| `origines_xp` | `naturelle`, `spawn_mmo` | **Anti-farm** : d'où doit venir la créature pour donner XP et butin de combat. |

Un objet donné s'écrit `{ "id": …, "min": 1, "max": 1, "chance": 1.0 }` ;
`min`, `max` et `chance` sont optionnels.

`categorie` : `animal`, `bete`, `bete_fantasy`, `reptile`, `insecte`,
`humanoide`, `humanoide_monstrueux`, `orc`, `mort_vivant`, `elementaire`,
`miniboss`, `boss`, `autre`.
`rarete` : `commune`, `peu_commune`, `rare`, `exotique`, `tres_rare`,
`legendaire`.
`origines_xp` : `naturelle`, `spawn_mmo`, `elevage`, `spawner`, `oeuf`,
`commande`, `invocation`, `inconnue`.

### Ce qui se passe quand un fichier est fautif

Une faute n'empêche jamais le serveur de démarrer et ne disparaît jamais en
silence. Deux niveaux :

- **Erreur** — le fichier est écarté, les autres se chargent quand même. JSON
  mal formé (le message donne la ligne et la colonne), mot de vocabulaire
  inconnu (le message énumère les valeurs acceptées), niveau au-dessus de
  `niveau_max`.
- **Avertissement** — le fichier est chargé, mais quelque chose mérite l'œil.
  Objet ou type d'entité absent de cette installation, chance nulle, carcasse
  démesurément longue.

Une référence inconnue est volontairement un avertissement et non une erreur :
un datapack peut légitimement décrire une créature d'un mod optionnel.

Tout est visible en jeu avec `/metiers inspecter erreurs`, et dans le journal du
serveur au démarrage.

## Les matières

Dix objets, pas un de plus. Chaque fois qu'un équivalent existait déjà dans le
modpack, il a été gardé — la petite peau est le cuir de lapin, la peau commune
vient de FleshZ, les écailles brutes de Lands of Icaria, le tanin est l'écorce
de Farmer's Delight. Ne sont créés que les paliers vides, et les deux fourrures
dont les équivalents portent un nom d'espèce (« Wolf Fur » sur un tigre serait
illisible).

| Matière | Identifiant | Origine prévue |
|---|---|---|
| Peau épaisse | `peau_epaisse` | Dépeceur 21 |
| Peau rare | `peau_rare` | Dépeceur 41 |
| Fourrure commune | `fourrure_commune` | Dépeceur 13 |
| Fourrure épaisse | `fourrure_epaisse` | Dépeceur 24 |
| Fourrure rare | `fourrure_rare` | Dépeceur 40 |
| Tendon | `tendon` | Dépeceur 25, occasionnel |
| Fourrure travaillée | `fourrure_travaillee` | Travailleur du cuir 23 |
| Écailles préparées | `ecailles_preparees` | Travailleur du cuir 33 |
| Cuir exotique | `cuir_exotique` | Travailleur du cuir 38 |
| Cuir rare | `cuir_rare` | Travailleur du cuir 45 |

Tous dans l'onglet créatif « Haute Capitale — Matières ». Aucun n'a de
comportement : ce sont des matières, les recettes qui les consomment vivent dans
des fichiers de données. Chaque objet du mod porte une description dans son
infobulle : les gadgets la dessinent eux-mêmes, les matières, la baguette et
les textiles la lisent dans les fichiers de langue (`item.<id>.desc`, un
retour à la ligne par ligne). Les textures sont générées par `tools/textures/` ;
quatre sont des recolorations de textures MIT — voir `CREDITS.md`.

### Les dix tags

Les recettes demanderont une **famille** de matière, jamais un objet précis :
ainsi la fourrure de loup d'un autre mod vaut la nôtre, et personne ne perd ce
qu'il possède déjà.

| Tag | Contenu |
|---|---|
| `#haute_capitale_metiers:peaux/petites` | `minecraft:rabbit_hide` |
| `#…:peaux/communes` | `fleshz:hide` |
| `#…:peaux/epaisses` | `peau_epaisse`, `hmobs:brown_bear_hide` |
| `#…:peaux/exotiques` | `landsoficaria:aeternae_hide` |
| `#…:peaux/rares` | `peau_rare` |
| `#…:fourrures/communes` | `fourrure_commune`, `more_rpg_classes:wolf_fur` |
| `#…:fourrures/epaisses` | `fourrure_epaisse`, `more_rpg_classes:polar_bear_fur` |
| `#…:fourrures/rares` | `fourrure_rare` |
| `#…:ecailles` | `landsoficaria:myrmeke_scales`, `slug_scales`, `hazennstuff:shadow_scale` |
| `#…:tanin` | `farmersdelight:tree_bark` |

Toute entrée venant d'un autre mod est déclarée `"required": false` : si le mod
manque, le tag se charge quand même, sans lui. Un tag qui exigerait un objet
absent échouerait en entier, silencieusement pour le joueur.

### FleshZ

FleshZ 1.6.1 (MIT) est **porté en 1.21.11** dans son propre projet,
`../fleshz-1.21.11-port/`, avec sa licence et son auteur. On en garde les objets
(`fleshz:hide`, `fleshz:prepared_hide`) et les onze séchoirs ; on en retire la
mécanique d'obtention du cuir, qui ferait doublon avec le dépeçage et le tannage
du MMO : tuer une vache redonne le butin vanilla. Le détail des retraits et des
changements d'API est dans son `PORTAGE.md`.

## Les recettes de métier

Rien à voir avec une recette d'établi : pas de grille, pas de livre de
recettes. Une liste d'ingrédients **par famille**, un prix, un résultat, et le
niveau à partir duquel le métier la propose.

`data/<namespace>/hcm/recipes/<metier>/<recette>.json` — le chemin est la clé :
ce fichier décrit `haute_capitale_metiers:travailleur_du_cuir/tannage_simple`.

```json
{
  "titre": "Tannage simple",
  "metier": "travailleur_du_cuir",
  "niveau": 9,
  "xp": 39.0,
  "ingredients": [
    { "tag": "haute_capitale_metiers:peaux/communes", "quantite": 3, "nom": "Peau commune" },
    { "tag": "haute_capitale_metiers:tanin", "quantite": 1, "nom": "Écorce" }
  ],
  "cout": 1,
  "resultat": { "id": "minecraft:leather", "quantite": 1 }
}
```

| Champ | Défaut | Rôle |
|---|---|---|
| `metier` | — | Obligatoire. L'atelier de ce métier propose la recette |
| `niveau` | 1 | Niveau requis |
| `xp` | 0 | XP à niveau égal ; la décote s'applique ensuite |
| `ingredients` | — | Obligatoire. Chacun donne `tag` **ou** `id`, `quantite` (1) et un `nom` d'affichage facultatif |
| `cout` | 0 | Prix en `monnaie` |
| `resultat` | — | Obligatoire. `id` et `quantite` (1) |
| `titre` | nom du résultat | Le libellé de la ligne |

Un ingrédient inconnu ou un résultat inconnu **rejette** la recette : elle
serait proposée au joueur et échouerait au clic. Une famille vide n'est pas une
erreur — elle se remplira quand le mod qui la nourrit sera installé — mais se
voit dans `/metiers inspecter recettes`.

### La fabrication

Tout se joue sur le fil du serveur, en une passe : on **vérifie tout** — recette,
métier, niveau, ingrédients, monnaie — sans rien toucher ; puis, seulement si
tout passe, on **retire** et on **remet**. Rien ne peut s'intercaler entre les
deux, ni un autre clic, ni une déconnexion. Un résultat qui ne tient pas dans
l'inventaire tombe aux pieds du joueur, jamais perdu. Le moteur ne fait pas
confiance à l'écran : chaque demande est revérifiée, y compris celles que
l'interface n'aurait jamais dû permettre.

Ce qui s'affiche dans l'atelier vient entièrement du serveur, calculé avec
l'inventaire réel du joueur : *« Peau commune 6/3 · Écorce 0/1 · 1 M · +39 XP
(100 %) »*. Le client trie, filtre, et renvoie l'identifiant de la recette
cliquée — rien d'autre.

### Les neuf recettes du Travailleur du cuir

| Recette | Niv. | Entrée | Écorce | Sortie |
|---|---|---|---|---|
| Assemblage de chutes | 1 | 3 petites peaux | — | Peau commune |
| Tannage simple | 9 | 3 peaux communes | 1 | Cuir |
| Tannage robuste | 16 | 3 peaux épaisses | 2 | Cuir robuste (More RPG Classes) |
| Traitement de fourrure | 23 | 4 fourrures communes | 2 | Fourrure travaillée |
| Traitement de fourrure épaisse | 31 | 3 fourrures épaisses | 2 | Fourrure travaillée |
| Préparation d'écailles | 33 | 4 écailles | 2 | Écailles préparées |
| Tannage exotique | 38 | 3 peaux fantasy | 3 | Cuir exotique |
| Tannage rare (peau) | 45 | 3 peaux rares | 3 | Cuir rare |
| Tannage rare (fourrure) | 45 | 3 fourrures rares | 3 | Cuir rare |

L'XP de départ suit `3 × niveau + 12` — un point de départ à régler en jeu,
fichier par fichier.

## Les nodes

Un node est un filon, un buisson, une touffe de fleurs : un bloc que l'on
récolte avec un métier de récolte, qui se vide, et qui repousse. Deux choses à
distinguer :

- **Le type** vit dans les datapacks — `data/<ns>/hcm/nodes/<metier>/<nom>.json`
  décrit `<ns>:<metier>/<nom>`. C'est la fiche : quel bloc, quel métier, quel
  niveau, quel butin, quel délai.
- **Les nodes eux-mêmes** — *où* ils sont — vivent dans la sauvegarde du monde,
  posés un par un par l'administrateur, à la baguette ou à la commande. Chaque
  dimension a les siens.

**Aucune mécanique ne détruit un bloc.** Récolter remplace le *bloc plein* par
le *bloc vide* ; repousser fait l'inverse. Un node ne se casse jamais, même en
créatif — il se retire. Et en survie, avec `proteger_les_blocs`, aucun bloc ne
se casse du tout.

### Fiche de node

```json
{
  "metier": "mineur",
  "niveau": 5,
  "xp": 13,
  "bloc_plein": "minecraft:iron_ore",
  "bloc_vide": "minecraft:stone",
  "loot": [
    { "id": "minecraft:raw_iron", "min": 1, "max": 2 }
  ],
  "respawn": 300,
  "coups": 3,
  "outil": "minecraft:pickaxes",
  "titre": "Filon de fer",
  "commentaire": "Base de tout le Forgeron d'apprenti."
}
```

| Champ | Défaut | Rôle |
|---|---|---|
| `metier` | obligatoire | le métier qui récolte |
| `niveau` | 1 | en dessous, le node est incassable |
| `xp` | 0 | XP à niveau égal ; la décote de la configuration s'applique ensuite |
| `bloc_plein` | obligatoire | le bloc quand le node est disponible — `id` ou `id[propriete=valeur]`, comme `/setblock` |
| `bloc_vide` | obligatoire | le bloc une fois récolté (`minecraft:air` accepté) |
| `loot` | obligatoire | les objets donnés — `id`, `min`, `max`, `chance`, comme dans les fiches de créature |
| `respawn` | 300 | secondes avant que le bloc plein revienne |
| `coups` | 3 | clics gauches nécessaires pour récolter |
| `outil` | aucun | famille d'objets (tag) à tenir en main, ex. `minecraft:pickaxes` |
| `titre`, `commentaire` | — | pour les humains |

Un bloc inconnu est une **erreur** : la fiche est écartée, on ne pourrait ni la
poser ni la faire repousser. Un métier d'artisanat, une XP nulle, un butin vide
ou un objet de butin absent sont des avertissements.

Deux pièges de blocs vanilla, évités dans les fiches livrées : un buisson de
baies dépouillé (`age=1`) remûrit tout seul aux ticks aléatoires, hors du
registre — on lui préfère `dead_bush` comme bloc vide ; une géode bourgeonnante
fait pousser des bourgeons autour d'elle — on prend `amethyst_block`. Et une
plante a besoin de son support : posez le node là où le bloc plein tient
(une fleur sur de la terre).

### Les trente-deux types livrés

Mineur, pioche en main — `bloc_vide` : pierre (ardoise pour les deux derniers) :

| Type | Niv. | Bloc plein | Butin | Repousse | Coups |
|---|---|---|---|---|---|
| `mineur/cuivre` | 1 | minerai de cuivre | 1-3 cuivre brut | 3 min | 3 |
| `mineur/charbon` | 3 | minerai de charbon | 1-3 charbon | 3 min | 3 |
| `mineur/fer` | 5 | minerai de fer | 1-2 fer brut | 5 min | 3 |
| `mineur/or` | 15 | minerai d'or | 1-2 or brut | 7 min | 4 |
| `mineur/lapis` | 20 | minerai de lapis | 2-5 lapis | 7 min | 4 |
| `mineur/amethyste` | 25 | grappe d'améthyste | 1-2 éclats | 10 min | 4 |
| `mineur/diamant` | 35 | minerai de diamant (ardoise) | 1 diamant | 15 min | 5 |
| `mineur/emeraude` | 40 | minerai d'émeraude (ardoise) | 1 émeraude | 15 min | 5 |

Herboriste, à la main — `bloc_vide` : air :

| Type | Niv. | Bloc plein | Butin | Repousse | Coups |
|---|---|---|---|---|---|
| `herboriste/pissenlit` | 1 | pissenlit | 1-2 | 2 min | 1 |
| `herboriste/coquelicot` | 3 | coquelicot | 1-2 | 2 min | 1 |
| `herboriste/baies` | 6 | buisson de baies mûr | 2-4 baies | 4 min | 1 |
| `herboriste/champignon` | 8 | champignon brun | 1-3 | 3 min | 1 |
| `herboriste/muguet` | 12 | muguet | 1-2 | 4 min | 1 |
| `herboriste/orchidee` | 20 | orchidée bleue | 1 | 5 min | 2 |

L'XP suit `3 × niveau + 12`, la règle commune à toutes les actions du mod
(voir Équilibrage). Ces quinze-là sont des blocs vanilla : aucun mod requis.

Et les minerais des mods du pack — dix-sept filons de plus, pioche en main,
chacun écarté au chargement si son mod manque (comme une recette), ce que son
commentaire annonce :

| Type | Niv. | Bloc plein | Butin | Repousse | Coups |
|---|---|---|---|---|---|
| `mineur/lignite` | 2 | lignite (Icaria) | 1-3 lignite | 3 min | 3 |
| `mineur/chalkos` | 4 | chalkos (Icaria) | 1-3 chalkos brut | 4 min | 3 |
| `mineur/kassiteros` | 8 | kassiteros (Icaria) | 1-2 kassiteros brut | 5 min | 3 |
| `mineur/jaspe` | 8 | cristal de jaspe (Icaria) — le bourgeon reste | 1-2 éclats de jaspe | 7 min | 3 |
| `mineur/sideros` | 10 | sideros (Icaria) | 1-2 sideros brut | 5 min | 3 |
| `mineur/anthracite` | 12 | anthracite (Icaria) | 1-3 anthracite | 5 min | 3 |
| `mineur/dolomite` | 14 | dolomite (Icaria) | 1-3 dolomite | 5 min | 3 |
| `mineur/argent` | 18 | argent (Witcher) | 1-2 argent brut | 7 min | 4 |
| `mineur/argent_ardoise` | 20 | argent d'ardoise (Witcher) | 1-2 argent brut | 7 min | 4 |
| `mineur/sliver` | 22 | sliver (Icaria) | 1-2 sliver | 7 min | 4 |
| `mineur/fer_sombre` | 24 | fer sombre (Witcher) | 1-2 fer sombre brut | 7 min | 4 |
| `mineur/fer_sombre_ardoise` | 26 | fer sombre d'ardoise (Witcher) | 1-2 fer sombre brut | 8 min | 4 |
| `mineur/fer_sombre_nether` | 26 | fer sombre du Nether (Witcher) | 1-2 fer sombre brut | 8 min | 4 |
| `mineur/vanadium` | 30 | vanadium (Icaria) | 1-2 vanadium brut | 10 min | 4 |
| `mineur/zircon` | 30 | cristal de zircon (Icaria) — le bourgeon reste | 1-2 éclats de zircon | 10 min | 4 |
| `mineur/molybdene` | 34 | molybdène (Icaria) | 1-2 molybdène brut | 10 min | 5 |
| `mineur/meteorite` | 42 | météorite (Witcher) | 1 météorite | 15 min | 5 |

Les niveaux suivent la progression des métaux d'Icaria (chalkos, kassiteros,
sideros, vanadium, molybdène — les dagues du Forgeron) et du Sorceleur (argent,
fer sombre, météorite) ; le jaspe et le zircon arrivent juste avant la taille
du Joaillier (8 et 32). L'hyliastrum d'Icaria ne donne rien sans toucher de
soie : pas de filon. Un filon de plus s'ajoute fichier par fichier, sans
recompiler.

Et les plantes des mods du pack — vingt-neuf de plus pour l'Herboriste, à la
main, écartées de même si leur mod manque. C'est ce qui fait de lui **le
fournisseur de l'Alchimiste** (les herbes d'Icaria, une par effet de potion)
et **du Cuisinier** (les plantes sauvages de Farmer's Delight, ses légumes) :

| Type | Niv. | Bloc plein | Butin | Pour |
|---|---|---|---|---|
| `herboriste/trefle` | 2 | trèfle (Icaria) | 1-2 | chance |
| `herboriste/bouilloire_solaire` | 3 | sunkettle (Icaria) | 1-2 | célérité |
| `herboriste/larmes_de_lance` | 4 | speardrops (Icaria) | 1-2 | saut |
| `herboriste/carottes_sauvages` | 4 | carottes sauvages (FD) | 1-3 carottes | Cuisinier, vision nocturne |
| `herboriste/fraisier` | 5 | fraisier (Icaria) — reste un buisson sec | 2-4 fraises | Cuisinier |
| `herboriste/pommes_de_terre_sauvages` | 5 | pommes de terre sauvages (FD) | 1-3 | Cuisinier |
| `herboriste/betteraves_sauvages` | 6 | betteraves sauvages (FD) | 1-3 | Cuisinier |
| `herboriste/herbe_aveugle` | 7 | blindweed (Icaria) | 1-2 | vision nocturne |
| `herboriste/oignons_sauvages` | 8 | oignons sauvages (FD) | 1-3 oignons | Cuisinier |
| `herboriste/charmonder` | 9 | charmonder (Icaria) | 1-2 | résistance au feu |
| `herboriste/colonie_de_champignons_bruns` | 9 | colonie (FD) | 2-4 champignons | Cuisinier |
| `herboriste/namdrake` | 10 | namdrake (Icaria) | 1-2 | soin II |
| `herboriste/tomates_sauvages` | 10 | tomates sauvages (FD) | 1-3 tomates | Cuisinier |
| `herboriste/chameomille` | 11 | chameomile (Icaria) | 1-2 | régénération |
| `herboriste/colonie_de_champignons_rouges` | 11 | colonie (FD) | 2-4 champignons | Cuisinier |
| `herboriste/crocs_de_lion` | 12 | lionfangs (Icaria) | 1-2 | force |
| `herboriste/choux_sauvages` | 12 | choux sauvages (FD) | 1-3 choux | Cuisinier |
| `herboriste/garde_de_feu` | 13 | firehilt (Icaria) | 1-2 | force |
| `herboriste/hydracinthe_bleue` | 14 | blue hydracinth (Icaria) | 1-2 | respiration |
| `herboriste/riz_sauvage` | 14 | riz sauvage (FD) | 1-3 riz | Cuisinier |
| `herboriste/eponge_solaire` | 15 | sunsponge (Icaria) | 1-2 | respiration |
| `herboriste/dathulla` | 16 | dathulla (Icaria) | 1-2 | chute lente |
| `herboriste/lys_du_vide` | 22 | voidlily (Icaria) | 1-2 | invisibilité |
| `herboriste/bolbos` | 30 | bolbos (Icaria) | 1-2 | tortue |
| `herboriste/mondanos` | 40 | mondanos (Icaria) | 1 | chance |
| `herboriste/coton_d_orage_bleu` | 42 | blue stormcotton (Icaria) | 1-2 | vent |
| `herboriste/coton_d_orage_pourpre` | 44 | purple stormcotton (Icaria) | 1 | vent |
| `herboriste/psilocybos` | 45 | psilocybos (Icaria) | 1-2 | potions persistantes |
| `herboriste/agaric_des_mites` | 46 | moth agaric (Icaria) | 1 | potions persistantes |

Repousse de 2 à 15 minutes, un coup — deux pour les plantes rares. Soixante et
un types en tout : vingt-cinq filons, trente-six plantes.

### Récolter

Clic gauche sur un node. Le serveur vérifie, dans l'ordre : que c'est un node,
qu'il est plein (sinon, le temps restant), que le joueur exerce le métier, qu'il
a le niveau, qu'il tient l'outil. Puis il compte les coups — par joueur, oubliés
après quatre secondes de silence — et au dernier coup **il vide le node avant de
donner quoi que ce soit** : c'est le verrou. Deux joueurs qui frappent au même
instant ne récoltent pas deux fois ; le second trouve le node vide.

Le butin va dans l'inventaire, au sol s'il est plein. L'XP suit la décote de la
configuration : un Mineur de niveau 30 sur un filon de niveau 5 ramasse le fer,
pas l'XP.

Le clic droit est neutre sur un node en survie — sans cela, un buisson de baies
se cueillerait à la vanilla, sans métier.

### Repousser

Un node récolté porte une **heure de repousse**, en temps réel, sauvegardée avec
le monde. Aucun tick par node. On ne la compare qu'à trois moments :

- quand un joueur frappe — un node en retard repousse sous le coup ;
- quand un chunk se charge — les nodes en retard repoussent à la fin du tick ;
- au balayage, toutes les `nodes_balayage_secondes` — seulement les nodes en
  attente, seulement ceux dont le chunk est chargé.

Décharger le chunk, attendre, le recharger : le node est plein immédiatement.
Redémarrer le serveur : les nodes sont retrouvés, l'heure de repousse aussi —
un node vidé 40 s avant l'arrêt a 40 s de moins à attendre au retour.

Minecraft met le serveur en pause quand il est vide (`pause-when-empty-seconds`,
60 s par défaut) : le balayage s'arrête avec lui, mais l'heure de repousse,
elle, court toujours. Au retour d'un joueur, les nodes en retard repoussent au
premier balayage, au premier chargement de chunk ou au premier coup.

### Poser

L'administrateur, en créatif, avec la **baguette de node** (`outil_node`, onglet
« Matières ») :

```
/metiers node outil mineur/fer        une baguette liée au type
/metiers node lier herboriste/baies   relier la baguette en main
```

Clic droit sur un bloc : un node y est posé, le bloc plein apparaît. Clic droit
sur un node : sa fiche. Accroupi + clic droit sur un node : il est retiré, le
bloc reste tel quel. La baguette est inerte hors du mode créatif.

Sans client, tout se fait aussi par commande — voir plus bas.

## Le Chasseur

Tuer une créature déclarée donne de l'XP Chasseur — sauf si elle vient d'un
élevage, d'un générateur, d'un œuf ou d'une commande. C'est tout l'enjeu :
l'élevage massif ne doit rien rapporter, et personne ne doit pouvoir le
contourner après un rechargement.

### L'origine, marquée à l'apparition

Chaque mob porte son **origine**, écrite dans ses propres données, donc
conservée quand le chunk se décharge et quand le serveur redémarre. Trois
sources, par ordre de priorité :

1. **Une étiquette de commande** `hcm_origine_<origine>` sur l'entité :
   `/summon cubeanimals:tiger ~ ~ ~ {Tags:["hcm_origine_spawn_mmo"]}`. C'est le
   contrat offert aux systèmes du MMO et aux administrateurs — déclarer une
   origine sans une ligne de code. Elle l'emporte toujours.
2. **La raison d'apparition de Minecraft**, lue à l'initialisation du mob par
   le seul mixin du mod (voir Architecture) : apparition naturelle, structure,
   patrouille → `naturelle` ; générateur et générateur d'épreuve → `spawner` ;
   reproduction → `elevage` ; œuf, seau, distributeur → `oeuf` ; `/summon` →
   `commande` ; invocation par un sort ou un mod → `invocation`. Un mob converti
   (zombie noyé, villageois zombifié) garde l'origine de l'entité d'avant.
3. **Une déduction à l'entrée dans le monde**, pour ce qui n'a jamais été
   initialisé par Minecraft : un petit né d'une reproduction est un `elevage`
   (`AnimalEntity.breed` n'initialise pas le petit) ; le reste reçoit
   `origine_par_defaut` — `naturelle`, pour ne pas priver les chasseurs d'un
   monde déjà peuplé avant l'installation du mod.

Une origine écrite n'est jamais réécrite par une source plus faible : un mob de
générateur qui se recharge reste un mob de générateur.

Deux cas à connaître : un `/summon` **avec** NBT ne passe pas par
l'initialisation (comportement vanilla, pour ne pas écraser le NBT) et reçoit
donc l'origine par défaut — étiquetez-le si ça compte ; et un mod qui construit
ses entités sans passer par Minecraft les fait arriver sans marque, donc à
l'origine par défaut. `origine_par_defaut: "inconnue"` rend les deux cas
inéligibles tant qu'ils ne sont pas étiquetés.

`/metiers origine <cibles>` montre l'origine effective de n'importe quelle
créature, et `/metiers origine <cibles> <origine>` la règle.

### Ce qui se passe à la mort

Un seul écouteur, sur la mort de l'entité. Rien n'est fait si aucune fiche ne
décrit la créature, ni si aucun joueur n'est responsable.

- **Qui est crédité ?** Le joueur qui a porté le coup fatal — flèche comprise —
  ou, s'il n'y en a pas, celui qui l'a frappée dans les cinq dernières secondes,
  comme Minecraft le fait pour ses propres butins rares. Deux joueurs sur la même
  proie : un seul coup est fatal, un seul est crédité. Une chute, la lave, un
  autre mob : personne.
- **L'origine est-elle éligible ?** `origines_xp` de la fiche. Sinon : ni XP ni
  butin de combat, et le Chasseur lit pourquoi (« élevage — aucune XP »).
- **L'XP.** Rien sans le métier ; rien sous le `niveau` de la fiche (le joueur
  lit « niveau 24 de Chasseur requis ») ; sinon l'`xp` de la fiche, avec la
  **même décote** qu'en fabrication : un Chasseur 40 sur une proie de niveau 5
  tue, ramasse, mais ne progresse pas.
- **Le butin de combat** (`loot_combat`) tombe au sol pour tout joueur crédité,
  Chasseur ou non, si l'origine est éligible. C'est ainsi que les orcs — dont la
  table de butin est vide chez leur mod — lâchent enfin des Martins, sans
  qu'aucune table soit réécrite.
- **La viande** (`viande`) tombe aussi, mais **sans regarder l'origine** :
  élever pour manger est un jeu légitime, et Minecraft le permet déjà.

### Les fiches livrées

Quarante-sept fiches, dont trente-quatre proies — la table du Chasseur de l'audit
(§30), sur des créatures qui existent dans le pack : Cube Animals (marmotte 1,
fennec 4, capucin 5, chevreuil 6, antilope 9, aigle 12, renne 13, ours noir 18,
crotale 20, crocodile 22, komodo 23, tigre 24, léopard des neiges 26, élan 26,
bison 28, morse 28), H Mobs (zèbre 10, hyène 14, lion 25, ours brun 27 — GeckoLib),
panda roux 3, cerf de Deer Mod 8, loup 15, Icaria (capella
11, cerver 12, argan hound 18, crocotta 19, aeternae 35), et chez les orcs le
loup de guerre 17, le troll et l'ogre 39, le minotaure 40. Les orcs eux-mêmes ne
sont pas des proies (exclus à l'audit) : leurs treize fiches ne portent que des
Martins — de 1-3 pour un guerrier à 6-12, toujours, pour un chef. Les trois
civils ne lâchent rien.

`/metiers inspecter proies` liste tout ça par niveau, en grisant les créatures
absentes de l'installation. Une fiche pour une créature absente n'est pas une
erreur : elle attend son mod.

L'XP de chasse suit la règle commune `3 × niveau + 12` — 15 pour un lapin, 84
pour un tigre, 132 pour un minotaure — et non plus la table de l'audit (3 XP
pour un lapin), qui demandait vingt-cinq lapins pour le premier niveau et
trois fois plus de gibier que de recettes aux artisans. Voir Équilibrage.

## Équilibrage

Une seule règle pour tous les métiers : **une action de son niveau rapporte
`3 × niveau + 12` XP** — une recette, un node, une proie —, le dépeçage une
fois et demie (la carcasse est plus rare que le coup). Avec la courbe
`75 × N^1,1`, cela fait 25 à 35 actions par niveau du début à la fin : 5
filons de cuivre pour le niveau 2, 22 recettes de niveau 10 pour le 11, 33
recettes de niveau 40 pour le 41. La décote fait le reste : rien ne monte
sur du trivial.

C'est un réglage de fichiers. `tools/data/Equilibrage.java` réécrit l'XP de
toutes les fiches de créature et de node selon la règle (`java
tools/data/Equilibrage.java` depuis la racine) ; changer `base()` et relancer
change le rythme de tout le jeu. Les recettes portent la même formule dans
leurs générateurs. Pour un rythme différent par métier, éditer les fiches, ou
la courbe elle-même dans la configuration (`xp_base`, `xp_exposant`).

## Les carcasses et le Dépeceur

Une proie abattue laisse une **carcasse** — la seule entité du mod. Pas un
PNJ, pas un mob : une chose posée au sol, sans IA, qui se souvient de la
créature qu'elle était pour lui ressembler, et qu'un seul Dépeceur peut
travailler. Elle expire au bout de la durée de sa fiche et n'est **jamais
sauvegardée** : au redémarrage, il n'y a plus de carcasses, et rien n'est
dupliqué.

Elle n'apparaît que si l'origine de la créature est éligible (même règle que
l'XP et le butin de combat) : une bête d'élevage meurt à la vanilla, avec ses
propres butins, sans carcasse à travailler. C'est ce qui empêche la ferme à
cuir.

### Le rendu : la créature elle-même, couchée

La carcasse transporte au client un instantané de la créature morte — son type
et ses données. Le client la reconstruit en mannequin, la place où est la
carcasse, la couche — le roulis de 90° de la pose de mort de Minecraft,
appliqué par nous dans le repère du corps — et demande à *son* rendu de la
dessiner. Un cerf mort ressemble à un cerf, un loup à un loup, sans un seul
modèle à créer ; un renderer GeckoLib dessine sa créature comme d'habitude.
(On ne donne pas au mannequin un `deathTime` positif : le renderer vanilla s'en
servirait pour le coucher, mais GeckoLib s'en sert aussi pour le teinter de
rouge « blessé ».) Cette idée vient
de Flaying (tous droits réservés) ; aucune ligne n'en est reprise.

Si une famille de créatures refuse l'exercice, la carcasse passe au **rendu de
repli** — une fourrure posée à plat, à la taille de la bête — et le journal du
client le dit une fois par type. Rien ne plante. Le mannequin n'est jamais
ajouté au monde ni tické.

### Dépecer

Clic droit sur la carcasse, couteau en main. Ce n'est pas un clic, c'est une
**canalisation** : le Dépeceur reste en place, garde son outil, et au bout de
la durée de la fiche (`canalisation`, 2 à 6 s selon la bête) reçoit la
matière principale, les secondaires qui tombent, et l'XP — avec la même décote
qu'ailleurs. Bouger d'un bloc, s'éloigner, changer d'objet en main, voir la
carcasse disparaître : tout s'annule, sans rien donner, et il lit pourquoi.

Deux verrous font qu'un seul Dépeceur obtient tout : la carcasse se **réserve**
au premier qui commence — le second lit « quelqu'un dépèce déjà cette
carcasse » —, et se **réclame** avant toute distribution, sur le fil du
serveur. Si le premier renonce, elle se libère et le second peut s'y mettre.

Refus, dans l'ordre : pas de fiche · déjà dépecée · déjà en cours · pas le
métier · niveau insuffisant · pas le couteau · trop loin (`depecage_portee`,
3 blocs). Le Chasseur qui a tué n'a aucun droit particulier : n'importe quel
Dépeceur peut travailler sa proie.

### Les couteaux

Les outils sont ceux de Farmer's Delight, par palier — un tag par rang, tout
en `required: false` :

| Rang | Tag | Couteaux acceptés |
|---|---|---|
| Apprenti (1-10) | `couteaux` | silex, fer, or, diamant, netherite |
| Compagnon (11-20) | `couteaux/compagnon` | fer, or, diamant, netherite |
| Expert (21-30) | `couteaux/expert` | or, diamant, netherite |
| Maître (31-40) | `couteaux/maitre` | diamant, netherite |
| Grand Maître (41-50) | `couteaux/grand_maitre` | netherite |

Chaque fiche nomme son tag (`outil`) ; sans lui, c'est `depecage_outil` de la
configuration (`couteaux`). **Sans Farmer's Delight, ces tags sont vides et
personne ne peut dépecer** — le diagnostic le signale, et lève l'exigence
d'outil le temps de l'essai. `depecage_outil: ""` la lève pour de bon.

### Le bloc « depecage » d'une fiche

```json
"depecage": {
  "niveau": 22,
  "xp": 42,
  "duree_carcasse": 90,
  "canalisation": 5,
  "outil": "haute_capitale_metiers:couteaux/expert",
  "matiere": { "id": "haute_capitale_metiers:peau_epaisse", "min": 1, "max": 2 },
  "secondaires": [ { "id": "minecraft:bone", "min": 1, "max": 2, "chance": 0.5 } ]
}
```

| Champ | Défaut | Rôle |
|---|---|---|
| `niveau` | 1 | niveau de Dépeceur requis |
| `xp` | 0 | XP à niveau égal |
| `duree_carcasse` | 60 | secondes avant que la carcasse disparaisse |
| `canalisation` | 3 | secondes de dépeçage, sans bouger |
| `outil` | config | famille d'outils à tenir |
| `matiere` | obligatoire | la matière principale |
| `secondaires` | vide | ce qui peut venir en plus — tendon, os, corne, venin |

Les trente-quatre proies livrées ont leur bloc : petites peaux (`rabbit_hide`) pour
le petit gibier, peaux communes (`fleshz:hide`) pour les cervidés et bêtes
d'Icaria, fourrure commune pour le renne et le loup, fourrure épaisse pour les
ours, canidés de guerre et bêtes d'Icaria, fourrure rare pour le tigre et le
léopard, peau épaisse pour les reptiles, grands ongulés, morse et humanoïdes
monstrueux (1 à 2 seulement, décision de l'audit), `aeternae_hide` pour
l'aeternae. Le **tendon** n'est en secondaire qu'à partir du niveau 25 ; les
bois, cornes, venins et crânes des mods concernés viennent en plus. XP de
dépeçage = 1,5 × XP de chasse. Les Martins des orcs suivent désormais la table
de loot de l'audit : 1-3 (civils, une fois sur deux), 3-8 (guerriers), 10-25
(élites, toujours), 15-30 (troll, ogre, minotaure).

## Le Forgeron : fabrication et réparation

Le Forgeron est un artisan complet, et le nœud du réseau : il produit les
outils dont les autres métiers ont besoin pour commencer, et il répare tout ce
qui s'use — pour tout le monde.

### Fabrication

Douze recettes livrées, en fichiers comme les autres : les six composants
intermédiaires d'Epic Knights (hampe 4, garde 6, lingot et petites plaques
d'acier 8, coiffe matelassée 12, rangées lamellaires 18 — les mêmes
proportions que leurs recettes d'établi) et les six dagues d'Icaria (chert 1,
chalkos 3, kassiteros 11, sideros 21, acier de vanadium 31, orichalque 41),
qui sont **les couteaux du Dépeceur**, un par palier. La lanière de cuir
(`magistuarmory:leather_strip`), que la garde et les rangées consomment, est
une recette du Travailleur du cuir (niveau 2). Les 414 armes, armures et
boucliers du pack se répartiront ensuite fichier par fichier.

Une recette dont le résultat ou un ingrédient n'existe pas sur l'installation
est écartée au chargement (voir Ce qui se passe quand un fichier est fautif) :
sans Epic Knights, les six composants apparaissent en erreur dans
`/metiers inspecter erreurs`, et c'est voulu.

### Réparation universelle

Chez un PNJ dont le rôle porte `"reparation": true` — le forgeron livré —,
l'écran a deux onglets : **Fabrication**, filtré par niveau, et **Réparation**,
ouvert à n'importe quel joueur, sans métier. Un joueur qui n'exerce pas le
métier de l'atelier arrive directement sur Réparation : c'est ce qu'il vient
chercher. L'onglet liste les objets
réparables de l'inventaire avec leur durabilité et leur prix, un bouton par
ligne, et **Tout réparer** avec le total et le solde. Le serveur revérifie tout
au clic — durabilité réelle, solde — puis débite et répare dans le même
geste. « Tout réparer » est tout ou rien : s'il manque un Martin, rien ne se
passe.

Le prix se lit dans la configuration (`reparation`) :

```
prix = durabilité manquante × coefficient de rareté   (× brise si l'objet est brisé)
```

| Rareté | Coefficient (Martins par point) |
|---|---|
| commun | 0,05 |
| peu commun | 0,08 |
| rare | 0,12 |
| épique | 0,20 |
| légendaire (`#haute_capitale_metiers:legendaire`) | 0,35 |

Jamais moins que `minimum` (1). Une épée rare à 42 % de 1 500 points → 870 ×
0,12 = 105 Martins. La rareté est celle du composant vanilla de l'objet ; la
famille `legendaire` force le coefficient légendaire. Tout objet à durabilité,
de n'importe quel mod, se répare : vérifié sur une pioche vanilla, une élytre,
une cuirasse d'Epic Knights, une dague d'Icaria, une hache de Hazen N Stuff.

### Brisé plutôt que détruit

Les objets de la famille `#haute_capitale_metiers:reparable_si_brise` — par
défaut tout ce qui s'enchante par la durabilité : outils, armes, armures,
arcs, boucliers, élytres — **ne disparaissent plus** à zéro. Ils restent dans
l'inventaire, à leur dernier point, marqués brisés : nom en rouge, mention
BRISÉ dans l'infobulle, et surtout **plus d'attributs, plus d'outil, plus
d'arme, plus de parade** — une épée brisée ne frappe plus, une pioche brisée ne
creuse plus, un bouclier brisé ne pare plus. User encore un objet brisé ne le
détruit pas non plus. La marque garde ce qu'il faisait, et la réparation — au
tarif majoré, `brise` × 2,5 — le lui rend à l'identique.

C'est le second mixin du mod, sur `ItemStack.onDurabilityChange`, le seul
point par où passe toute usure, de tous les mods. Les rares mods qui gèrent
eux-mêmes la destruction de leurs objets se listent dans
`reparation.exceptions` : ceux-là disparaissent comme avant.

## L'Ingénieur et la Pierre de foyer

L'Ingénieur fabrique des gadgets : des objets qui lisent la carte, portent le
joueur, rangent son sac ou le protègent — jamais des objets qui la creusent.
Les deux mods de l'audit, MBK's Useful Items (CC0-1.0, Kotlin sans sources) et
Gadgets Against Grind (MIT, NeoForge), ne pouvaient pas être repris tels
quels ; leurs idées le sont, le code est réécrit en Java pour Fabric 1.21.11,
dans notre espace de noms, et les vingt-cinq icônes venues de MBK sont
redessinées en 16×16 (`tools/textures/MakeGadgets.java`) — les siennes, en
32×32 ombré, juraient avec le pixel-art du jeu ; celles de GAG, déjà en 16×16,
sont gardées. Ce qui a été écarté : tout ce
qui casse ou pose des blocs chez MBK (pioche explosive, foreuse, baguette de
construction, torche automatique). Ce qui a été adapté : le casque de mineur et
la lanterne de sac donnent de la vision nocturne dans l'obscurité au lieu de
poser de la lumière ; les bottes de lave donnent la résistance au feu au lieu
de figer la lave ; le kit de campement donne des effets au lieu de poser un
lit et un feu.

### Les trente et un gadgets

| Palier | Gadget | Ce qu'il fait |
|---|---|---|
| 6 | Sel sacré (×4) | Repoussant : aucun monstre n'apparaît à moins de 8 blocs pendant 5 min |
| 8 | Lunettes nocturnes | Vision nocturne tant qu'on les porte |
| 10 | Casque de mineur | Casque de fer ; vision nocturne dans l'obscurité |
| 12 | Aimant à butin | Marche/arrêt ; tire vers soi ce qui traîne à 6 blocs |
| 14 | Détecteur de minerai | Compte les minerais alentour, par famille |
| 15 | Broyeur automatique | Accroupi + clic droit fixe un filtre ; détruit cet objet du sac |
| 16 | Lanterne de sac | Vision nocturne dans l'obscurité, depuis le sac |
| 18 | Compresseur de poche | 9 lingots ou bruts → 1 bloc, dans le sac |
| 18 | Lunettes de prospection | Compte les minerais toutes les 10 s |
| 18 | Corde d'évasion | Remonte droit à la surface ; 8 usages |
| 20 | Grappin | S'élance vers le bloc visé (24 blocs) ; 128 usages |
| 22 | Chercheur de géode | Direction de la géode la plus proche (48 blocs) |
| 24 | Onguent sacré (×2) | Repoussant 16 blocs, 15 min |
| 24 | Lunettes de trésor | Une lueur sur chaque coffre à 12 blocs |
| 25 | Bottes stabilisatrices | Résistance au recul, chute sans danger +3, dégâts de chute −50 % |
| 26 | Résonateur de cristal | Vibre — et chante plus haut — près du diamant ou de l'émeraude |
| 28 | Balise de rappel | Accroupi + clic droit retient l'endroit ; clic droit **maintenu** 4 s y ramène (1500 blocs, 3 min) |
| 28 | Kit de secours (×2) | Vie pleine, faim comblée, effets néfastes retirés ; consommé |
| 30 | Kit de campement (×2) | Régénération II, saturation, résistance 30 s ; consommé |
| 30 | Sonde à écho | Écoute : cité antique ou profondeurs sombres à 96 blocs |
| 32 | Gants de portée | +2 blocs de portée, en main gauche |
| 34 | Baume sacré | Repoussant 24 blocs, 60 min |
| 34 | Kit de raffinage | Une minute durant, le brut du sac devient lingot |
| 35–41 | Exosquelette (4 pièces) | Armure de fer ; set complet : célérité, résistance, vision nocturne sous terre |
| 38 | Bottes de lave | Bottes de fer ; résistance au feu |
| 40 | Drone minier | Un drone suit le joueur et ramasse à 3 blocs ; clic droit le rappelle |
| 45 | Pierre de foyer améliorée | Recharge 5 min au lieu de 15, portée × 2 |

Les recettes vivent dans `hcm/recipes/ingenieur/`, XP = 3 × niveau + 12, en
Martins de 0 à 5 selon le palier ; l'exosquelette demande l'acier d'Epic
Knights, et ses quatre recettes sont écartées au chargement si le mod n'est pas
là. Trois ingrédients viennent de nos propres matières : le tendon des bottes
stabilisatrices, la fourrure commune du kit de campement, le cuir exotique des
gants.

Le drone est la seconde entité du mod, après la carcasse : sans IA, jamais
sauvegardée — au redémarrage, le gadget la redéploie —, elle vole vers
l'épaule de son maître et s'éteint si elle le perd (24 blocs, ou déconnexion).
À l'écran, c'est le drone du mod d'origine : son sprite face à la caméra, comme
une boule de neige, avec un léger balancement.
Les repoussants s'appuient sur le premier mixin, déjà là pour la chasse : un
monstre qui naît de lui-même — nature, générateur, patrouille, renfort — à
portée d'un joueur protégé est retiré avant d'avoir été vu ; un œuf ou une
commande ne sont jamais concernés.

### La Pierre de foyer

Parler à un PNJ dont le rôle porte `"foyer": true` — l'aubergiste livré — lie
le joueur à cet endroit et lui donne une Pierre de foyer s'il n'en a pas. La
pierre se tient (clic droit maintenu) : **5 secondes immobile**, puis le joueur
est chez lui. Bouger d'un bloc, être touché ou relâcher le clic interrompt la
canalisation. Ensuite, **15 minutes** de recharge (5 pour l'améliorée). À plus
de **4000 blocs** (8000 pour l'améliorée), la pierre est « trop faible pour
vous ramener d'ici » ; dans un autre monde, elle ne répond pas, sauf si la
configuration le permet. Pas de bloc, pas de lit, pas de foudre.

**Plusieurs auberges, au choix du joueur.** Chaque aubergiste visité s'ajoute
aux auberges que le joueur connaît — sous le nom du PNJ, celui que vous lui
avez donné — et devient son foyer du moment. Le joueur choisit ensuite où sa
pierre le ramène : **accroupi + clic droit sur la pierre** affiche la liste
de ses auberges dans le chat, chacune cliquable ; le registre de n'importe
quel aubergiste les liste aussi, avec la distance et un bouton « Choisir » ;
et `/metiers auberge` fait la même chose. La liste est un attachement du
joueur, sauvegardée, gardée à la mort, envoyée au client — l'infobulle de la
pierre nomme le foyer choisi. `auberges_max` (0, sans limite) plafonne le
nombre d'auberges retenues ; au-delà, la plus ancienne est oubliée. Une
sauvegarde d'avant la b9, qui ne portait qu'un foyer, est relue comme une
liste d'une auberge.

## Couturier, Joaillier et Cuisinier

Les trois derniers artisans n'ajoutent presque que des fichiers : l'atelier,
le moteur de recettes, la décote, la maîtrise et le prix en Martins étaient
là depuis l'étape 5. Ce que l'étape 11 apporte de neuf est commun à tous les
artisans — la qualité et les buffs de repas — plus une chaîne de matières pour
le Couturier, le seul des trois qui n'avait rien pour travailler.

### La qualité Excellent

Chaque objet fabriqué a une chance d'être **Excellent**, d'autant plus
grande que l'artisan domine la recette (voir `qualite` en configuration).
C'est un composant de données sur la pile — `haute_capitale_metiers:qualite`
—, jamais un second objet : un ragoût excellent reste un ragoût, du même
identifiant, mais les deux piles ne se mélangent pas. Il se voit : nom doré,
ligne « ✦ Excellent » dans l'infobulle, « ✦ 1 Excellent ! » à la fabrication.
Un plat Excellent porte un buff renforcé d'un tiers. Les matières et
composants — bobines, tissus, lingots, tannages, sel sacré… — déclarent
`"qualite": false` dans leur recette et restent toujours Normal : la qualité
ne se propage pas d'un ingrédient à l'objet fini, elle se joue à la dernière
fabrication.

### Le Couturier

La chaîne textile est celle de Weaver's Paradise (MIT, Vortianski), réécrite
dans notre espace de noms avec ses proportions et ses icônes de matières ; les
vêtements ont des icônes dessinées pour ce mod. Le rendu des vêtements sur le
corps n'existe pas, et c'est voulu : **un vêtement ne se porte pas**. C'est un
composant — la doublure d'une vraie armure, qui, elle, se porte. Les recettes
d'armure qui les consomment viendront avec la liste d'équipement du serveur,
métier par métier.

```
cotonnier (node Herboriste 3)  →  coton  →  fibre (×3)  →  bobine (4 fibres)  →  tissu (6 bobines)
                                                  laine, jean (coton + teinture), soie (ficelle ou soie d'arachné)
tissu  →  base de haut, manches, jambes  →  chemise, pantalon, pull, bonnet, bas, capes
tissu + composants d'autres mods  →  robes de Wizards, Armory, Elemental Wizards, Forcemaster, garbs de Bards,
                                     chapeaux d'Accents, sacoches de Runes, manteaux de mmo_accessories, étoffes de Hazen's Stuff
```

Quatre-vingt-huit recettes de 1 à 50 (`hcm/recipes/couturier/`), XP =
3 × niveau + 12. Le cotonnier est un bloc du mod en deux états — mûr et
jeune —, jamais posé par un joueur : c'est un node, l'administrateur le pose
avec la baguette ou `/metiers node poser haute_capitale_metiers:herboriste/coton`,
et il ne tombe jamais tout seul. Le Diadème étoilé (50) demande des gemmes du
Joaillier : les deux métiers se rejoignent en fin de progression.

### Le Joaillier

Aucun code, aucun objet : ses bijoux existent (`jewelry`, `mmo_accessories`)
et fonctionnent déjà avec Accessories et Trinkets. Cinquante-trois recettes
(`hcm/recipes/joaillier/`) : anneaux de cuivre, fer, or (1-6), la trousse
(5), les tailles de gemme d'Icaria (jaspe 8, zircon 32) et de Hazen's Stuff
(quartz rose 47), puis anneaux (12-31) et colliers (18-34) sertis par gemme,
les pièces de `mmo_accessories` (argent, acier, malachite, aigue-marine,
rage, météorique) et, de 36 à 46, tout le netherite. Les bijoux uniques
restent hors des recettes : plans de boss et quêtes.

### Le Cuisinier et les buffs de repas

Farmer's Delight (MIT, natif 1.21.11) est une **dépendance de données**, pas
un portage : nos recettes citent ses plats. Soixante-huit recettes
(`hcm/recipes/cuisinier/`), aux **mêmes ingrédients que chez Farmer's Delight**
— elles sont dérivées de ses propres recettes d'établi et de marmite —, plus
les plats d'Icaria (pain d'épeautre, gâteaux, ragoûts de cerver, catoblepas,
aeternae et thog), de Hazen's Stuff et de Cube Animals. Un plat cuisiné chez
le PNJ porte un **buff de repas** ; le même plat sorti d'une marmite n'en
porte pas.

Le buff est une fiche de données, `hcm/buffs/<id>.json` :

```json
{
  "titre": "Vigueur",
  "attribut": "minecraft:max_health",
  "operation": "pourcent_base",
  "valeur": 0.05,
  "duree": 900
}
```

Quinze fiches livrées : convalescence (régénération naturelle), endurance,
vigueur guerrière (mêlée), œil du chasseur (distance), vigueur (vie
maximale), célérité, peau dure (résistance), esprit clair (puissance de sort)
et les écoles feu, givre, nature, lumière, soif de sang, pas feutrés, festin.
Les attributs viennent de Minecraft, de Puffish Attributes et de Spell Power ;
un attribut absent de l'installation est signalé au chargement et le buff ne
fait rien. La recette d'un plat nomme son buff et sa valeur —
`"repas": { "buff": "haute_capitale_metiers:melee", "valeur": 0.057 }` —, de
+3 % au niveau 10 à +12 % au niveau 50, festins à +10 % pendant trente
minutes.

Mécanique : le plat cuisiné reçoit dans son composant `consumable` un effet
d'ingestion à nous (`haute_capitale_metiers:repas`), à côté de ceux qu'il a
déjà — nourriture et confort de Farmer's Delight ne sont pas des buffs et
restent tels quels. À l'ingestion, le buff en cours est retiré et le nouveau
posé : **un seul buff de repas à la fois**. Il vit comme un modificateur
d'attribut temporaire — réappliqué à la connexion depuis un attachement du
joueur — doublé d'un effet de statut « Repas » qui ne fait rien d'autre que se
voir dans l'interface avec son compte à rebours. Boire du lait retire l'effet,
et le buff avec. **Mourir retire le repas.** Aucun mixin.

Les festins (poulet rôti, jambon glacé, hachis Parmentier, citrouille farcie,
salade scintillante) sont fabriqués en quatre portions, chacune porteuse du
buff « festin » : la portion, pas le bloc, est ce qui se mange.

### La marmite bridée

Sans rien changer à Farmer's Delight, un datapack intégré au jar
(`haute_capitale_metiers:marmite`, activé par défaut) remplace les quatorze
recettes de marmite dont le plat est de niveau Cuisinier 20 ou plus par un
fichier portant une condition de chargement toujours fausse : la recette
disparaît, la marmite reste pour le bouillon, les soupes simples, le riz, la
sauce tomate et la décoration. Un joueur ne contourne plus son niveau en
posant une marmite chez lui. `/datapack disable "haute_capitale_metiers:marmite"`
rend la marmite complète.

## L'Alchimiste

Le onzième métier, ajouté après les étapes à la demande du serveur : il
fabrique **les potions du jeu**, et rien d'autre — aucun objet nouveau, aucun
effet inventé. Trente-trois recettes (`hcm/recipes/alchimiste/`), de 1 à 50 :
soin, célérité, saut, vision nocturne, résistance au feu, régénération, force,
respiration aquatique, chute lente, invisibilité, tortue, chance, vent ; les
paliers II à partir du niveau 18, prolongés à partir de 26, jetables à partir
de 36, persistants à partir de 46.

Chaque recette demande une **fiole** et **une plante de l'Herboriste** — une
plante par effet : la bouilloire solaire pour la célérité, la garde-de-feu pour
la force, le lys du vide pour l'invisibilité… — plus un réactif selon le palier
: poudre lumineuse (II), redstone (prolongée), poudre à canon (jetable),
psilocybos et agaric des mites d'Icaria (persistante). Les deux premières
potions se font avec les fleurs vanilla, pissenlit et coquelicot. Une potion
n'est jamais Excellent.

Mécanique : le résultat d'une recette peut porter des **composants** —

```json
"resultat": {
  "id": "minecraft:potion",
  "composants": { "minecraft:potion_contents": { "potion": "minecraft:healing" } }
}
```

— gardés en JSON brut au chargement et décodés avec les registres du serveur au
moment de la fabrication, parce que le contenu d'une potion nomme une entrée
de registre. C'est ouvert à toute recette : un objet enchanté, nommé, teint
s'écrit de la même façon. `tools/recipes/MakeAlchemyRecipes.java` régénère les
trente-trois fichiers.

## Les PNJ de métier

Le mod ne crée **aucune entité** et n'utilise **aucun villageois**. Vous posez
vos Easy NPC comme d'habitude, avec vos skins et vos noms ; il suffit de leur
attacher une action.

Dans l'éditeur d'actions d'Easy NPC :

| Champ | Valeur |
|---|---|
| Événement | `ON_INTERACTION` |
| Type | `CUSTOM` |
| Commande | `haute_capitale_metiers:profession forgeron` |

Le dernier mot est l'identifiant du rôle. Un rôle d'un autre espace de noms
s'écrit en entier (`mon_pack:forgeron`).

Easy NPC (MIT, Markus Bordihn) est une **dépendance**, jamais modifiée : le mod
se contente d'enregistrer une action dans son registre public. Aucun mixin. Si
Easy NPC est absent ou si son API change, le serveur démarre quand même et
`/metiers atelier` continue d'ouvrir les mêmes interfaces.

### Fiche de rôle

`data/<namespace>/hcm/roles/<role>.json` — même règle que partout : le chemin est
la clé.

```json
{
  "interface": "atelier",
  "metier": "forgeron",
  "titre": "Forge",
  "accueil": "Alors, on forge quoi aujourd'hui ?",
  "peut_enseigner": true
}
```

| Champ | Défaut | Rôle |
|---|---|---|
| `interface` | `atelier` | `atelier`, `formation` ou `registre` |
| `metier` | — | Obligatoire pour `atelier` et `formation` |
| `titre` | déduit du métier | Le titre en haut de l'écran |
| `accueil` | — | Une ligne sous le titre |
| `peut_enseigner` | `true` | Un maître peut-il faire apprendre son métier |
| `reparation` | `false` | Le PNJ propose-t-il l'onglet Réparation, ouvert à tous — voir Le Forgeron |

Les trois formes d'interface :

- **`atelier`** — l'établi d'un artisan. Les sept métiers d'artisanat en ont un.
  La liste des recettes arrive à l'étape 5 ; pour l'instant l'écran montre la
  progression du joueur dans ce métier et le dit franchement.
- **`formation`** — un maître de métier : le joueur voit sa progression et, s'il
  ne connaît pas encore le métier, un bouton pour l'apprendre.
- **`registre`** — la vue d'ensemble des dix métiers du joueur. L'aubergiste.

### Les dix-neuf rôles livrés

`forgeron`, `couturier`, `travailleur_du_cuir`, `joaillier`, `cuisinier`,
`ingenieur`, `alchimiste` (ateliers) · un maître par métier, `maitre_mineur`,
`maitre_herboriste`, `maitre_chasseur`, `maitre_depeceur`, `maitre_forgeron`,
`maitre_travailleur_du_cuir`, `maitre_couturier`, `maitre_joaillier`,
`maitre_cuisinier`, `maitre_ingenieur`, `maitre_alchimiste` (formations) ·
`aubergiste` (registre, et lie le foyer : `"foyer": true`).

**Seuls les maîtres enseignent.** Un atelier fabrique, répare pour le
forgeron, mais ne forme jamais : apprendre un métier, c'est aller voir son
maître, là où vous l'avez posé. Un maître de plus — un autre lieu, un autre
espace de noms — tient en un fichier :

```json
{ "interface": "formation", "metier": "chasseur", "titre": "Maître chasseur" }
```

## Commandes

| Commande | Permission | Effet |
|---|---|---|
| `/metiers voir` | tous | Ses propres métiers |
| `/metiers voir <joueur>` | game master | Les métiers d'un joueur |
| `/metiers voir <joueur> <metier>` | game master | Le détail d'un métier |
| `/metiers apprendre <joueur> <metier>` | game master | Fait apprendre un métier |
| `/metiers oublier <joueur> <metier>` | game master | Retire un métier |
| `/metiers xp <joueur> <metier> <quantite>` | game master | Ajoute de l'XP |
| `/metiers niveau <joueur> <metier> <valeur>` | game master | Fixe le niveau |
| `/metiers reinitialiser <joueur> [<metier>]` | game master | Remet à zéro |
| `/metiers inspecter resume` | game master | Ce que le serveur a retenu des fichiers |
| `/metiers inspecter liste [<categorie>]` | game master | Toutes les fiches de créature |
| `/metiers inspecter creature <id>` | game master | Le détail d'une fiche |
| `/metiers inspecter roles [<role>]` | game master | Les rôles de PNJ, et leur détail |
| `/metiers inspecter recettes [<metier>]` | game master | Les recettes, avec leurs familles vides |
| `/metiers inspecter recette <id>` | game master | Le détail d'une recette, et la décote pour vous |
| `/metiers inspecter erreurs` | game master | Les problèmes du dernier chargement |
| `/metiers atelier <role> [<joueur>]` | game master | Ouvre une interface sans PNJ |
| `/metiers fabriquer <joueur> <recette> [<fois>]` | game master | Fabrique comme le ferait le clic — même moteur, mêmes refus |
| `/metiers inspecter nodes` | game master | Les types de node chargés |
| `/metiers node outil [<type>]` | game master | Donne la baguette, liée au type s'il est donné |
| `/metiers node lier <type>` | game master | Relie la baguette en main |
| `/metiers node poser <type> <x y z>` | game master | Pose un node |
| `/metiers node retirer <x y z>` | game master | Retire un node, le bloc reste |
| `/metiers node info <x y z>` | game master | Type, métier, plein ou temps restant |
| `/metiers node liste` | game master | Les nodes de la dimension, par type |
| `/metiers node vider <x y z>` | game master | Vide un node comme après une récolte, sans rien donner |
| `/metiers node remplir [<x y z>]` | game master | Fait repousser un node, ou tous ceux en attente |
| `/metiers node frapper <joueur> <x y z>` | game master | Un coup, comme le clic gauche — même moteur, mêmes refus |
| `/metiers inspecter proies` | game master | Les proies déclarées, par niveau, avec leurs origines éligibles |
| `/metiers origine <cibles>` | game master | L'origine effective de créatures (`@e[…]`), et si elle est éligible |
| `/metiers origine <cibles> <origine>` | game master | Règle leur origine |
| `/metiers abattre <joueur> <cibles>` | game master | Abat comme si le joueur avait porté le coup — même écouteur, mêmes règles |
| `/metiers inspecter depecage` | game master | Les créatures dépeçables : niveau, matière, durées, couteau |
| `/metiers carcasse creer <type> <x y z> [<secondes>]` | game master | Pose une carcasse sans tuer |
| `/metiers carcasse liste` | game master | Les carcasses de la dimension et leur état |
| `/metiers carcasse purger` | game master | Retire toutes les carcasses |
| `/metiers depecer <joueur> <carcasse>` | game master | Commence à dépecer comme le ferait le clic — mêmes refus, même canalisation |
| `/metiers reparer <joueur>` | game master | Ce que l'onglet Réparation montrerait à ce joueur |
| `/metiers reparer <joueur> <emplacement>` | game master | Répare un objet, comme le ferait le clic |
| `/metiers reparer <joueur> tout` | game master | Tout réparer, tout ou rien |
| `/metiers briser <joueur>` | game master | Brise l'objet en main du joueur |
| `/metiers inspecter recettes ingenieur` | game master | Les trente recettes de l'Ingénieur |
| `/metiers foyer <joueur>` | game master | Le foyer de ce joueur |
| `/metiers foyer <joueur> lier` | game master | Lie son foyer là où il se tient, comme l'aubergiste |
| `/metiers foyer <joueur> choisir <numero>` | game master | Choisit, pour lui, l'auberge de ce numéro |
| `/metiers foyer <joueur> effacer` | game master | Efface son foyer et toutes ses auberges |
| `/metiers auberge` | tous | Ses auberges, cliquables pour choisir |
| `/metiers auberge choisir <numero>` | tous | Choisit l'auberge où la pierre ramène |
| `/metiers gadget utiliser <joueur> [<case>]` | game master | Joue le clic droit, côté serveur, sur l'objet en main — ou sur la case de barre donnée |
| `/metiers repas <joueur>` | game master | Le buff de repas en cours de ce joueur |
| `/metiers repas <joueur> <buff> [<valeur>] [<secondes>]` | game master | Pose un buff de repas sans plat |
| `/metiers repas <joueur> effacer` | game master | Retire le repas |
| `/metiers recharger` | admin | Recharge la configuration **et les datapacks** |
| `/metiers diagnostic` | admin | Diagnostic serveur du socle — voir Tests |

`/metiers recharger` fait les deux : la configuration d'équilibrage et les
fichiers de contenu, en une seule commande. Le `/reload` de Minecraft recharge
aussi les fichiers de contenu — c'est le même mécanisme.

## Construire et tester

```bash
gradle build           # compile, exécute les 1 836 tests hors jeu, produit le jar
gradle runSocleTest    # les tests du socle seuls
gradle runDonneesTest  # les tests du moteur de données seuls
gradle runServer       # serveur dédié de test
gradle runClient       # client de test
```

Le jar sort dans `build/libs/haute-capitale-metiers-0.1.0.b12.jar`. Il ne contient
que notre code : Easy NPC reste dans `libs/` et n'est jamais redistribué.

Le code client vit dans `src/client/java`, un source set séparé. Sur un serveur
dédié ces classes ne sont pas présentes du tout — aucune interface ne peut y
être chargée par accident.

## Tests

Deux niveaux, **2 341 vérifications au total**, toutes vertes.

**1 836 hors du jeu** — `gradle build`.

- *76, le socle.* Les dix métiers, les rangs, la courbe d'XP, les passages de
  niveau simples et multiples, le plafond, les valeurs invalides, l'indépendance
  entre métiers, l'immuabilité de l'état, la sérialisation en JSON **et en NBT**.
- *1 760, les données.* Lecture d'une fiche complète et d'une fiche vide, valeurs
  par défaut, aller-retour d'écriture, champs obligatoires, vocabulaire inconnu
  et qualité du message d'erreur, valeurs aberrantes ramenées dans le domaine
  utilisable, origines d'apparition, règle « le chemin est la clé », compte rendu
  de chargement, fiches de rôle et leur cohérence, et les matières : chaque
  texture est 16×16, a son modèle, son calque `items/` (sans lui rien ne
  s'affiche) et son nom dans les deux langues — et de même pour les trente et
  un gadgets, tous en 16×16 sauf la pierre améliorée, animée ; chaque tag ne
  nomme que des matières qui existent et déclare facultatif tout objet d'un autre mod. Les
  fiches et les rôles livrés dans le jar sont relus au passage : une faute de
  frappe dedans casse la compilation. Et les nodes : la fiche de type, ses
  défauts et ses refus, le registre d'un monde — carte par position, index par
  chunk, ensemble en attente — et son aller-retour NBT, puis les quatorze types
  livrés (dossier = métier, blocs vanilla, pioche pour le Mineur, main nue pour
  l'Herboriste). Et la chasse : chacune des dix-neuf raisons d'apparition de
  Minecraft se traduit dans notre vocabulaire, les étiquettes se relisent, et
  les quarante-trois fiches livrées respectent les décisions de l'audit (les
  orcs ne sont pas des proies, les civils ne lâchent rien, le chef lâche
  toujours, aucune fiche ne rend l'élevage éligible). Et le dépeçage : les
  champs `canalisation` et `outil` et leurs défauts, puis les trente blocs
  livrés — même niveau que la chasse, le couteau du palier, une matière d'une
  famille connue, le tendon réservé aux niveaux 25 et plus, une à deux peaux
  pour les humanoïdes monstrueux — et les cinq tags de couteaux, tous
  facultatifs, chaque palier inclus dans le précédent. Et la réparation : le
  barème par défaut, le rôle forgeron seul à réparer, les familles
  `reparable_si_brise` et `legendaire`, les douze recettes du Forgeron (XP =
  3 × niveau + 12, dagues sur les cinq paliers). Et l'étape 10 : les réglages
  des gadgets et du foyer, leurs bornes (une valeur folle est ramenée dans le
  domaine), le rôle aubergiste seul à lier le foyer, et les trente recettes de
  l'Ingénieur — une par gadget sauf la pierre simple, offerte ; résultat nommé
  comme le fichier ; nos ingrédients existent, les autres sont nommés ; paliers
  6, 8, 12, 20, 30, 40, 45 présents. Et l'étape 11 : la chance d'Excellent
  (0 sous l'écart, 5 % à l'écart, +1,5 % par niveau, plafond, bornes), les
  champs `qualite` et `repas` d'une recette, la fiche de buff (complète,
  minimale, opérations nommées, opération inconnue refusée en citant les
  valeurs acceptées) et les quinze livrées (titrées, en pourcentage
  raisonnable, dix minutes au moins, attributs de Minecraft, Puffish et Spell
  Power seulement) ; les 88 recettes du Couturier (matières jamais Excellent,
  vêtements oui, produits finis des seuls mods RPG du pack, 1 coton → 3
  fibres, le diadème qui demande des gemmes), les 53 du Joaillier (tailles
  jamais Excellent, bijoux oui, douze netherite entre 36 et 46), les 68 du
  Cuisinier (58 plats à buff existant, valeur qui suit le niveau, cinq festins
  en quatre portions, familles `c:` de Farmer's Delight), les quatorze
  recettes de marmite retirées — chacune vise un plat de niveau 20+ et porte
  sa condition fausse, et les soupes simples restent — et le cotonnier (node
  Herboriste 3, deux coups, nos deux blocs, notre coton). Chaque texture
  textile a son modèle, son calque, son nom ; le cotonnier son état de bloc et
  son modèle en croix ; l'effet repas son icône.

**505 en jeu** — `/metiers diagnostic` sur un serveur. Ce sont les scénarios que
le calcul pur ne peut pas atteindre : écriture et relecture réelles des données
du joueur, transfert à la mort, transfert au changement de dimension, et la
construction des écrans de rôle pour deux joueurs différents, et les matières
avec les registres réels : dix objets enregistrés et nommés, dix tags non vides,
et la compatibilité vérifiée mod par mod — `wolf_fur` de More RPG Classes est
bien accepté comme fourrure commune quand le mod est là, et le tag se charge
quand même quand il n'y est pas. Et la fabrication, sur un vrai inventaire :
XP entière au bon niveau, 25 % à +12, nulle à +25 mais l'objet produit, refus
sans rien consommer, dix demandes pour trois possibles → exactement trois,
maîtrise à la quinzième, ×5 réservé aux recettes maîtrisées, inventaire plein
sans perte, et le prix en Martins débité au centime. Et les nodes, 74
vérifications sur deux nodes posés en hauteur au-dessus du point d'apparition
et deux joueurs en mémoire : poser (bloc plein, registre, doublon et type
inconnu refusés), les refus sans métier, sans niveau, sans pioche — le bloc
intact —, trois coups puis la récolte (pierre, pas de l'air ; fer brut ; XP
pleine), le second joueur qui trouve le node vide, la repousse par balayage
une fois l'horloge avancée, le rechargement du chunk (repousse programmée,
appliquée à la fin du tick), deux joueurs à 2/3 chacun → un seul récolte, la
mémoire des coups, la décote (niveau 30 sur un filon 5 : fer, 0 XP), la
cueillette à la main, la protection (un node ne se casse pas ; en survie rien
ne se casse, en créatif le créatif n'est pas concerné), les crochets Fabric
eux-mêmes (clic gauche pris en main, clic droit neutre en survie), la
persistance NBT du registre du monde et la baguette. Et la chasse, 45
vérifications sur de vrais lapins apparus par les vrais chemins de Minecraft :
chaque chemin laisse la bonne marque (naturel, générateur, œuf, commande, une
reproduction réelle par `AnimalEntity.breed`, une étiquette qui l'emporte, une
entité jamais initialisée), la marque survit à l'écriture et à la relecture de
l'entité et n'est jamais réécrite ; puis de vraies morts par de vrais dégâts :
+15 XP pour un lapin sauvage, rien sans le métier, rien pour un petit né sous
les yeux du diagnostic, rien pour un œuf, une commande ou un générateur, XP
nulle pour un Chasseur 40, B crédité et A non quand B porte le coup fatal, A
crédité quand une chute achève sa proie, personne quand aucun joueur n'y est
pour rien ; enfin niveau requis, décote à 25 %, passage de niveau, butin de
combat et viande au sol, viande maintenue pour un élevage, et les messages.
Et les carcasses, 53 vérifications : tuer un lapin en laisse une, de lapin, là
où il est tombé, à sa taille, avec sa fiche, libre, expirant à l'heure de la
fiche — par le moteur et par de vrais dégâts ; un loup en laisse une de loup ;
un petit d'élevage n'en laisse pas ; l'instantané transporte type, données
SNBT nettoyées et dimensions, la boîte est couchée ; les refus (métier,
niveau, couteau du palier, trop loin) ne touchent à rien ; le Dépeceur
commence, la carcasse lui est réservée, à mi-parcours rien, au bout de la
canalisation la peau et l'XP, la carcasse disparue, le Chasseur sans rien ;
décote à 0 pour un Dépeceur 30 ; deux Dépeceurs — le second lit « déjà en
cours », un seul obtient tout, le verrou ne cède qu'une fois, et si le premier
s'éloigne le second peut s'y mettre ; un pas de côté, un changement d'objet,
une carcasse disparue interrompent ; expiration à 61 s ; plafond par zone ; et
les messages. Et la réparation, 47 vérifications : le barème (100 points
manquants → 5, minimum, rareté, brisé × 2,5) ; une pioche usée jusqu'au dernier
point par la vraie usure — le mixin — reste dans l'inventaire, brisée, sans
outil ni attributs, et user encore ne la détruit pas ; épée sans arme,
bouclier sans parade ; réparée pour 32 Martins débités au centime, outil et
attributs revenus à l'identique ; un Martin de moins → refus, rien ne bouge ;
tout réparer : total exact, tout ou rien ; une pioche vanilla, une élytre, une
cuirasse d'Epic Knights, une dague d'Icaria, une hache de Hazen N Stuff
réparées ; l'onglet chez le forgeron seulement ; les cisailles en exception
disparaissent comme en vanilla ; et une dague de chert forgée par un Forgeron
niveau 1, refusée sans le métier. Le diagnostic construit des joueurs en mémoire et exerce sur eux les chemins de code du jeu —
même attachement, même écriture NBT, mêmes événements Fabric.

```
/metiers diagnostic
```

Ce n'est pas un joueur connecté, donc deux choses restent hors de portée :
l'affichage côté client et la traversée réelle du réseau. Un passage manuel avec
un vrai client reste utile avant la mise en production.

Sur un pack complet, un mod tiers peut écouter le même événement que nous et
supposer un vrai joueur (Xaero, à la réapparition) : le diagnostic écrit alors
une ligne `SAUT`, ni comptée réussie ni échouée, et dit quel écouteur l'a
interrompu.

La partie Chasse a besoin d'un chunk dont les **entités** sont chargées — ce
qui, sans joueur en ligne, n'arrive jamais tout seul : les entités d'un chunk
se chargent à part, de façon asynchrone. Le diagnostic force alors son chunk
d'essai, comme `/forceload`, et demande de relancer la commande quelques
secondes plus tard ; il le libère une fois fini.

Le moteur de données se vérifie autrement, parce que le rechargement à chaud et
les registres de Minecraft n'existent que sur un serveur qui tourne : on dépose
un datapack fautif dans `world/datapacks/`, on lance `/metiers recharger`, et on
lit `/metiers inspecter erreurs`. C'est la procédure suivie pour valider
l'étape 2.

Les nodes ont eu en plus leur passage sur un vrai serveur multi-mods, par
commandes : poser, lister, vider, redémarrer le serveur — les nodes sont
retrouvés, l'heure de repousse a continué de courir pendant l'arrêt —,
recharger un chunk avec un node en retard — il est plein au démarrage —, et
attendre les deux vraies minutes d'un coquelicot pour le voir repousser par le
balayage. Le clic gauche et le clic droit ont été vérifiés par leurs crochets
Fabric ; le geste lui-même, avec un vrai client, reste à faire sur le serveur
de production.

La chasse aussi : sur le vrai serveur, un lapin, un capella et un cerver
d'Icaria invoqués par commande, marqués, l'un réglé « générateur », le serveur
redémarré — les trois origines retrouvées telles quelles.

Les carcasses aussi : trois carcasses posées par commande sur le vrai serveur —
un capella et un aeternae d'Icaria (GeckoLib), un loup —, le serveur
redémarré : plus aucune, aucune erreur. Le **rendu**, lui, a été vu avec un
client de dev sur le serveur dev : loup et lapin vanilla, chevreuil de Cube
Animals, et lion, zèbre et ours brun de H Mobs (GeckoLib) couchés côte à côte,
sans teinte ni erreur (`shots/carcasses-4.png`) ; puis la boucle entière — un
chevreuil abattu par l'écouteur de mort, sa carcasse, la jauge de dépeçage, la
peau et l'XP (`shots/carcasses-5.png`, `carcasses-6.png`). Le repli à plat
reste là pour une famille qui refuserait l'exercice.

L'onglet Réparation a été vu avec un client de dev (`shots/reparation-1.png`) :
la forge s'ouvre directement sur Réparation pour un joueur qui n'est pas
Forgeron, le solde, une ligne par objet avec son pourcentage et son prix, une
épée BRISÉE dont le bouton se grise quand le solde ne suffit pas, « Tout
réparer » avec le total et le solde — et deux objets réparés par de vrais clics
sur Réparer, débités et retirés de la liste. Le message « X est brisé — un
forgeron peut le réparer » est arrivé en rouge dans le chat au moment du bris.

## Architecture

```
net.hautecapitale.metiers
├── HauteCapitaleMetiers      point d'entrée
├── api.Metiers               API publique — tous les systèmes passent par là
├── profession.Profession     les dix métiers
├── profession.Rank           rangs dérivés du niveau, jamais stockés
├── profession.ProfessionProgress   niveau + XP d'un métier
├── profession.ProfessionsState     l'ensemble des métiers d'un joueur
├── profession.ProfessionAttachments  persistance et synchronisation
├── profession.XpCurve        courbe et calcul des passages de niveau
├── config.MetiersConfig      source de vérité des valeurs d'équilibrage
├── data.DataRegistry         registre de données rechargeable à chaud
├── data.HcmData              les registres du mod, et leurs validations
├── data.LoadReport           erreurs et avertissements d'un chargement
├── creature.CreatureProfile  la fiche d'une créature
├── creature.DropEntry        un objet donné, avec quantité et probabilité
├── creature.DropRoll         le tirage d'une liste d'objets donnés, partagé
├── creature.CreatureEnums    vocabulaire des fiches
├── npc.NpcRole               ce qu'un PNJ sait faire pour un joueur
├── npc.RoleGate              seul chemin d'entrée dans une interface de métier
├── npc.ProfessionMenu        l'écran, côté serveur
├── npc.ProfessionScreenData  ce que le serveur envoie au client
├── npc.ProfessionNetwork     les deux messages de l'interface
├── npc.EasyNpcBridge         façade vers Easy NPC, isolée et facultative
├── item.HcmItems             les dix matières et leur onglet créatif
├── craft.CraftRecipe         une recette de métier
├── craft.CraftEngine         vérifier tout, puis retirer et remettre — atomique
├── craft.XpFalloff           la décote
├── craft.Mastery             les trois états de maîtrise
├── craft.MasteryAttachment   le compteur par recette, attaché au joueur
├── node.NodeType             la fiche d'un type de node
├── node.NodeStore            les nodes d'un monde — par position, par chunk, en attente
├── node.NodeAttachment       ce registre, sauvegardé avec le monde
├── node.NodeEngine           poser, frapper, vider, repousser — et la protection des blocs
├── node.NodeTool             la baguette de l'administrateur
├── node.NodeFeedback         ce que le joueur lit en frappant
├── hunt.OriginAttachment     l'origine d'une créature, écrite sur l'entité
├── hunt.OriginMarker         qui décide de l'origine : étiquette, raison d'apparition, déduction
├── hunt.HuntEngine           la mort d'une proie : crédit, éligibilité, XP, butin
├── hunt.HuntFeedback         ce que le Chasseur lit
├── mixin.MobEntityMixin      le premier mixin : lire la raison d'apparition (chasse, repoussants)
├── entity.HcmEntities        les deux entités : la carcasse, le drone
├── entity.CarcassEntity      ce qu'une proie laisse — instantané, verrous, expiration
├── skin.SkinningEngine       commencer, tenir, finir : la canalisation du Dépeceur
├── skin.SkinningFeedback     ce que le Dépeceur lit
├── repair.RepairEngine       la réparation universelle : prix, débit, remise en état
├── repair.Breakage           brisé plutôt que détruit, et ce qu'on rend à la réparation
├── repair.BrokenComponent    la marque, avec ce que l'objet faisait
├── repair.RepairData         l'onglet Réparation, tel que le serveur l'envoie
├── repair.RepairFeedback     ce que le joueur lit
├── mixin.ItemStackMixin      le second mixin : intercepter la destruction
├── gadget.GadgetItems        les trente et un gadgets et leur onglet créatif
├── gadget.GadgetComponents   actif, filtre, position, drone, échéance — ce qu'un gadget retient
├── gadget.Gadgets            ce que les gadgets partagent : config, messages, obscurité, direction
├── gadget.ToggleGadgetItem   aimant, compresseur, broyeur — marche/arrêt depuis le sac
├── gadget.WornGadgetItem     lunettes, casque, lanterne, bottes, exosquelette — tant qu'on porte
├── gadget.ScannerItem        minerai, géode, écho, résonateur — lire la carte
├── gadget.KitItem            secours, campement, raffinage
├── gadget.RepellentItem      sel, onguent, baume — et l'effet repoussant
├── gadget.EscapeRopeItem     droit vers le ciel
├── gadget.RopeLauncherItem   le grappin
├── gadget.ChanneledTeleportItem  tenir, ne pas bouger, partir — la canalisation commune
├── gadget.RecallBeaconItem   la balise : retenir un endroit, y revenir
├── gadget.MiningDroneEntity  le drone : suivre, ramasser, s'éteindre
├── gadget.MiningDroneItem    déployer, rappeler
├── hearth.HearthAttachment   le foyer d'un joueur, sauvegardé, gardé à la mort
├── hearth.HearthLink         parler à l'aubergiste : lier, offrir la pierre
├── hearth.HearthstoneItem    la Pierre de foyer, simple ou améliorée
├── quality.Quality           Normal ou Excellent — un composant, jamais un second objet
├── meal.MealBuff             la fiche d'un buff de repas
├── meal.MealConsumeEffect    l'effet d'ingestion posé sur le plat cuisiné
├── meal.MealEngine           un seul buff : poser, remplacer, retirer, réappliquer
├── meal.MealAttachment       le repas en cours, sauvegardé, pas gardé à la mort
├── meal.PotRestriction       le datapack intégré qui bride la marmite
├── textile.TextileItems      la chaîne textile du Couturier, quarante objets
├── textile.CottonBlocks      le cotonnier, mûr et jeune — le node de coton
├── command.MetiersCommands   commandes d'administration
├── command.DataCommands      commandes d'inspection des données
├── command.RoleCommands      rôles et repli par commande
├── command.RecipeCommands    recettes, et fabrication par commande
├── command.NodeCommands      nodes, et coup par commande
├── command.HuntCommands      origines, et mise à mort par commande
├── command.SkinCommands      carcasses, et dépeçage par commande
├── command.RepairCommands    réparation et bris par commande
├── command.GadgetCommands    foyer par commande, et le clic droit joué par le serveur
├── command.MealCommands      le buff de repas par commande
└── command.DiagnosticPlayer  le joueur en mémoire des diagnostics (FakePlayer de Fabric, neuf à chaque fois)

net.hautecapitale.metiers.client        (source set client uniquement)
├── ProfessionScreen          l'écran, côté joueur
├── CarcassRenderer           la créature reconstruite, couchée — ou la peau à plat
└── MiningDroneRenderer       le sprite du drone face à la caméra (rendu des boules de neige), flottant à l'épaule
```

Ajouter un domaine de données à une étape suivante tient en trois gestes :
déclarer un `DataRegistry` dans `HcmData`, écrire son validateur, l'enregistrer
dans `HcmData.init()`. Le reste — lecture, rechargement à chaud, rapport
d'erreurs, commandes d'inspection — est déjà là.

Points de conception qui tiennent pour la suite du projet :

- **Serveur autoritaire.** Toute méthode d'écriture prend un `ServerPlayerEntity`.
  Le client reçoit l'état et l'affiche ; il ne décide jamais.
- **Deux mixins, minuscules.** L'API d'attachement de Fabric couvre la
  persistance, la copie à la mort et la synchronisation client. Ce que Minecraft
  n'expose nulle part : la raison d'apparition d'un mob — lue en tête de
  `MobEntity.initialize` — et le moment où un objet usé disparaît — intercepté en
  tête de `ItemStack.onDurabilityChange`. Aucun des deux ne change un comportement
  quand le mod n'a rien à dire.
- **Aucune constante d'équilibrage dans le code.** Tout passe par la config.
- **Aucun contenu dans le code.** Aucune créature, aucun objet, aucun niveau
  n'est nommé en Java. Tout vit dans les datapacks et se recharge à chaud —
  jusqu'aux buffs de repas, qui sont des fiches.
- **La qualité est un composant, le buff un effet d'ingestion.** Pas de plat
  dupliqué, pas de mixin pour savoir qu'on mange : Minecraft porte les deux.
- **Un fichier fautif ne fait jamais tomber le serveur.** Il est écarté seul, et
  le problème reste consultable en jeu.
- **Aucun PNJ, aucun villageois.** Les PNJ restent ceux du joueur, gérés par
  Easy NPC ; le mod n'y ajoute qu'une action. Les deux entités du mod — la
  carcasse, le drone — sont des choses sans IA, jamais sauvegardées.
- **Le client n'affiche que ce qu'on lui donne.** L'écran ne contient aucune
  règle : ni niveau à comparer, ni droit à évaluer. Un client modifié ne peut
  rien s'accorder.
- **Une dépendance tierce ne doit pas pouvoir empêcher le démarrage.** Le pont
  Easy NPC est isolé dans sa propre classe, testé à l'exécution, et remplacé par
  une commande s'il échoue.
- **État immuable.** Chaque modification produit un nouvel état, ce qui déclenche
  la resynchronisation automatiquement.
- **Une seule porte d'entrée.** Les systèmes suivants appellent `Metiers`, jamais
  l'attachement directement.
- **Aucune mécanique ne creuse la carte.** Un node passe de plein à vide et de
  vide à plein ; rien n'est détruit, rien n'est généré. Le cassage de blocs est
  refusé en survie par configuration, et refusé sur un node dans tous les modes.
  Aucun gadget de l'Ingénieur ne casse ni ne pose de bloc : ceux de l'audit
  qui le faisaient ont été écartés ou adaptés.
- **Aucun tick par node.** Une heure de repousse, comparée seulement au coup,
  au chargement du chunk et à un balayage léger des nodes en attente.
