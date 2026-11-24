# Nexus BB1 — ajout et répartition des modèles

Version Minecraft : **1.21.11 / Fabric**. Rapport du **24 novembre 2026**.
Base conservée : **PRE35-SI1-OPT3**. Version ajoutée : **OPT3.BB1**.

## Contenu

L’archive contient 91 modèles : 41 coiffures, 30 modèles de pilosité et 20 paires d’oreilles, avec leurs 91 textures. `visage_rase.geo.json` étant vide, il correspond au choix « Aucune » déjà présent. **90 nouveaux choix 3D** sont donc ajoutés : 41 coiffures, 29 barbes/moustaches et 20 oreilles, sans doublon « visage rasé ».

Les fichiers géométriques et PNG sont embarqués sans modification. Il ne s’agit pas de skins reconstruits. Les barbes comportant des bijoux les conservent ; les ornements intégrés ne sont pas cumulés avec les ornements génériques de Nexus.

## Répartition des nouveaux choix

| Race | Coiffures | Barbes/moustaches 3D | Oreilles |
|---|---:|---:|---:|
| Humains | 36 | 17 | 0 |
| Nordiques | 29 | 20 | 0 |
| Nains | 36 | 27 | 0 |
| Elfes des bois | 31 | 0 | 14 |
| Hauts-elfes | 27 | 0 | 14 |

Les modèles partagés sont comptés dans chaque race concernée ; ces colonnes ne s’additionnent pas en modèles uniques.

- **Humains** : Choix polyvalents, coupes sobres et citadines ; styles nobles/de mage ; moustache impériale et barbe royale.
- **Nordiques** : Coupes guerrières, rasées et tressées ; deux variantes de tresses nordiques et barbe viking réservées à ce groupe.
- **Nains** : Grand choix de tresses, coupes robustes et barbes épaisses ; forge, mineur, runes, clan et royauté naine.
- **Elfes des bois** : Coupes libres, sauvages, tressées ou d’éclaireuse ; oreilles sylvestres, larges, pennées et repliées.
- **Hauts-elfes** : Coupes lisses, structurées et élégantes ; noblesse haut-elfique exclusive ; oreilles ornées, dorées, runiques et cristallines.

### Règles conservées

- Tous les anciens choix restent disponibles ; seuls les nouveaux modèles sont répartis par race.
- Les elfes restent imberbes, selon la règle existante. Les oreilles nouvelles sont réservées aux deux races elfiques.
- La barbe obligatoire des nains n’est pas supprimée par le modèle « visage rasé ».
- Aucune race n’est créée. « Draconique », « lunaire », « cristalline » et « pennée » sont traités comme des noms esthétiques d’assets, pas comme un ajout au lore.
- Les couleurs existantes restent accessibles. Les nouvelles coiffures suivent la palette cheveux, les barbes la palette de pilosité et les oreilles la teinte de peau.

## Installation

1. Fermer Minecraft et arrêter le serveur avant remplacement ; conserver une sauvegarde habituelle.
2. Remplacer **uniquement l’ancien JAR NexusCharacters** par `Nexus-BB1-1.21.11.jar`, côté client **et** côté serveur. Ne pas conserver deux JAR NexusCharacters simultanément.
3. Conserver les autres mods, la liaison Indivis-Nexus-NPC, les configurations et les données existantes. Aucun mod supplémentaire n’est requis pour ces modèles.

Les choix sont dans les commandes habituelles « Coiffure », « Pilosité » et « Oreilles » de la création/édition Nexus. Les tenues et les corps ne sont pas remplacés par ce pack.

## Vérifications effectuées sur ce binaire

| Vérification | Résultat |
|---|---|
| Géométries et textures de l’archive | 91 couples validés, 2 968 cubes natifs, dimensions UV et hiérarchies contrôlées |
| Client Minecraft réel, nouveaux modèles | 91 modèles parcourus ; 182 captures face/dos ; soumissions des 90 modèles non vides vérifiées |
| Palettes nouvelles | 76 variantes vérifiées : 12 cheveux, 48 barbes (dont blondes/rousses/violettes), 16 peau/oreilles ; alpha et bijoux contrôlés |
| Anciennes géométries | 320 états et 91 poses comparés à OPT3 : empreintes identiques |
| Anciens menus et première personne | 23 scènes de menus, bras, poses et réflexion HUD : bancs de régression passés |
| EasyNPC réel | 5 nouvelles recettes raciales, corps classiques/slim, export éditeur, validation serveur et rendu de 10 modèles distincts |
| Redémarrage du monde de test | 15 PNJ retrouvés, mêmes UUID et recettes ; 5 tags sentinelles préservés ; nouveaux modèles rendus après chargement |
| API commune côté serveur | 292 contrôles de catalogue/normalisation avec les classes client explicitement interdites au chargement |
| Audit du code existant | 261 classes inchangées ; 217 corps de méthodes conservés dans les 12 classes adaptées |
| Assets existants | 4 966 entrées inchangées octet pour octet |

Les aperçus du ZIP proviennent des captures du client réel. Le parcours de chaque modèle et les scènes combinées ne constituent pas une vérification de toutes les combinaisons possibles de vêtements, animations, shaders et mods. Ce travail n’inclut pas de mesure FPS sur Lion-Port ou de connexion à ton serveur de production. Le moteur de surfaces OPT3 est conservé ; les géométries nouvelles sont compilées une fois et les textures colorées sont réutilisées en cache (256 variantes maximum).

Le banc isolé utilise Fabric Loader 0.19.5, Fabric API 0.141.6, Skin Layers 1.10.2, EasyNPC 7.16.0 et la liaison Indivis-Nexus-NPC 0.1.0-PREVIEW1. Les journaux de ce banc hors connexion contiennent aussi des avertissements Mojang/Realms et de manifeste de persistance non configuré ; ils ne sont pas présentés comme une validation de la synchronisation de production.

### Identité du fichier testé

- SHA-256 BB1 : `a5f0c4a211a64503e30ffb93a6499a9172b7c22183542b18fc2e1a4cd231b10f`.
- SHA-256 base OPT3 : `de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b`.

## Ajouter d’autres assets ensuite

Le catalogue des modèles et la répartition sont fondés sur des données. Dans les sources, ajouter le couple geo.json/PNG, son libellé et les races autorisées dans `distribution.json`, puis reconstruire. Pour une nouvelle coiffure, attribuer un ID libre dans `hair_ids.json` (à partir de 541), sans modifier les anciens. Les ajouts de PNG de l’ancien système restent pris en charge. Installer la nouvelle version reconstruite sur les clients et le serveur.

Les nouveaux modèles doivent utiliser le sous-ensemble contrôlé : box UV, os imbriqués, rotations d’os et dilation des cubes. Les formats non pris en charge sont refusés explicitement. Déposer n’importe quel fichier Blockbench sans reconstruction n’est pas présenté comme une fonction disponible.

## Catalogue exact par modèle

H = Humains ; N = Nordiques ; D = Nains ; EB = Elfes des bois ; HE = Hauts-elfes. « — » signifie non proposé pour cette race.

### Coiffures

| Modèle source | H | N | D | EB | HE |
|---|:---:|:---:|:---:|:---:|:---:|
| `boucle.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `boucles_mi_longs.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `cheveux_longs.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `cheveux_ondules.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `chignon.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `chignon_guerrier.geo.json` | — | Oui | Oui | Oui | — |
| `chignon_messy.geo.json` | Oui | Oui | Oui | Oui | — |
| `court_classique.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `court_degrade.geo.json` | Oui | Oui | Oui | — | — |
| `court_ebouriffe.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `court_stylise.geo.json` | Oui | Oui | Oui | — | — |
| `crete_sauvage.geo.json` | Oui | Oui | Oui | Oui | — |
| `degrade_rase.geo.json` | Oui | Oui | Oui | — | — |
| `demi_attache.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `double_queue.geo.json` | Oui | — | Oui | Oui | Oui |
| `ebouriffe.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `elfe_noble.geo.json` | — | — | — | — | Oui |
| `frange_longue.geo.json` | Oui | — | Oui | Oui | Oui |
| `frange_longue_2.geo.json` | Oui | — | Oui | Oui | Oui |
| `long_degrade.geo.json` | Oui | — | Oui | Oui | Oui |
| `long_lisse.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `long_messy.geo.json` | Oui | Oui | Oui | Oui | — |
| `longs_tresses.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `meches_rebelles.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `ondule.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `ondules_romantiques.geo.json` | Oui | — | Oui | Oui | Oui |
| `queue_de_cheval.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `queue_de_cheval_2.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `rase_motif.geo.json` | Oui | Oui | Oui | — | — |
| `rase_undercut.geo.json` | Oui | Oui | Oui | — | — |
| `style_assassine.geo.json` | Oui | — | — | Oui | — |
| `style_elfique.geo.json` | — | — | — | Oui | Oui |
| `style_guerrier.geo.json` | Oui | Oui | Oui | Oui | — |
| `style_mage.geo.json` | Oui | — | — | — | Oui |
| `style_noble.geo.json` | Oui | — | — | — | Oui |
| `style_sauvage.geo.json` | Oui | Oui | Oui | Oui | — |
| `tresse_laterale.geo.json` | Oui | Oui | Oui | Oui | Oui |
| `tresses_elegantes.geo.json` | Oui | — | Oui | Oui | Oui |
| `tresses_laterales.geo.json` | Oui | — | Oui | Oui | Oui |
| `tresses_viking.geo.json` | — | Oui | Oui | — | — |
| `tresses_viking_2.geo.json` | — | Oui | Oui | — | — |

### Barbes et moustaches

| Modèle source | H | N | D | EB | HE |
|---|:---:|:---:|:---:|:---:|:---:|
| `barbe_ancienne.geo.json` | — | Oui | Oui | — | — |
| `barbe_blanche.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_carree.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_courte.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_epaisse.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_forge.geo.json` | — | — | Oui | — | — |
| `barbe_grisonnante.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_guerrier.geo.json` | — | Oui | Oui | — | — |
| `barbe_longue.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_mineur.geo.json` | — | — | Oui | — | — |
| `barbe_montagne.geo.json` | — | Oui | Oui | — | — |
| `barbe_pleine.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_royale.geo.json` | Oui | — | Oui | — | — |
| `barbe_royale_naine.geo.json` | — | — | Oui | — | — |
| `barbe_runique.geo.json` | — | — | Oui | — | — |
| `barbe_seigneur_profondeurs.geo.json` | — | — | Oui | — | — |
| `barbe_sombre.geo.json` | Oui | Oui | Oui | — | — |
| `barbe_totemique.geo.json` | — | Oui | Oui | — | — |
| `barbe_tressee.geo.json` | — | Oui | Oui | — | — |
| `barbe_tressee_clan.geo.json` | — | — | Oui | — | — |
| `barbe_viking.geo.json` | — | Oui | — | — | — |
| `barbichette.geo.json` | Oui | Oui | Oui | — | — |
| `bouc_van_dyke.geo.json` | Oui | Oui | Oui | — | — |
| `moustache_barbichette.geo.json` | Oui | Oui | Oui | — | — |
| `moustache_classique.geo.json` | Oui | Oui | Oui | — | — |
| `moustache_fine.geo.json` | Oui | Oui | Oui | — | — |
| `moustache_imperiale.geo.json` | Oui | — | — | — | — |
| `style_aventurier.geo.json` | Oui | Oui | Oui | — | — |
| `style_noble.geo.json` | Oui | — | Oui | — | — |
| `visage_rase.geo.json` | Rasé | Rasé | — | — | — |

### Oreilles

| Modèle source | H | N | D | EB | HE |
|---|:---:|:---:|:---:|:---:|:---:|
| `anneaux.geo.json` | — | — | — | Oui | Oui |
| `bijou_dore.geo.json` | — | — | — | — | Oui |
| `classique.geo.json` | — | — | — | Oui | Oui |
| `courbee.geo.json` | — | — | — | Oui | Oui |
| `cristalline.geo.json` | — | — | — | — | Oui |
| `draconique.geo.json` | — | — | — | — | Oui |
| `elancee.geo.json` | — | — | — | Oui | Oui |
| `en_angle.geo.json` | — | — | — | Oui | Oui |
| `fine.geo.json` | — | — | — | Oui | Oui |
| `large.geo.json` | — | — | — | Oui | — |
| `longue.geo.json` | — | — | — | Oui | Oui |
| `lunaire.geo.json` | — | — | — | — | Oui |
| `ornementee.geo.json` | — | — | — | — | Oui |
| `pennee.geo.json` | — | — | — | Oui | — |
| `relevee.geo.json` | — | — | — | Oui | Oui |
| `repliee.geo.json` | — | — | — | Oui | — |
| `runique.geo.json` | — | — | — | — | Oui |
| `sauvage.geo.json` | — | — | — | Oui | — |
| `sombre.geo.json` | — | — | — | Oui | — |
| `sylvestre.geo.json` | — | — | — | Oui | — |

