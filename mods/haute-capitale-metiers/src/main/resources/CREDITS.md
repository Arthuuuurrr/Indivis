# Crédits et licences tierces

« Haute Capitale — Métiers » réutilise des travaux de tiers. Ce fichier dit
lesquels, sous quelle licence, et ce qui en a été fait. Il est copié dans le jar
publié : la mention doit voyager avec le mod.

---

## Textures d'objet dérivées

### Wandering Trapper — MIT

- Auteurs : **cnlimiter, lilypuree**
- Source : https://github.com/Nova-Committee/wandering_trapper
- Licence : MIT

Quatre de nos textures sont des **recolorations** de trois de ses textures. Les
formes sont celles de Wandering Trapper ; seules les palettes ont changé.

| Notre texture | Source | Traitement |
|---|---|---|
| `fourrure_commune` | `wandering_trapper:item/fox_pelt` | Désaturée vers un brun neutre, pour qu'elle convienne au loup, au renard et à l'ours noir plutôt qu'au seul renard |
| `fourrure_epaisse` | `wandering_trapper:item/polarbear_pelt` | Assombrie vers le gris-beige, pour ne pas lire « ours polaire » |
| `fourrure_rare` | `wandering_trapper:item/snow_fox_pelt` | Reflets bleutés et liseré clair |
| `fourrure_travaillee` | `wandering_trapper:item/fox_pelt` | Teinte plus profonde, contour ajouté, deux points de couture |

### FleshZ — MIT

- Auteur : **Globox_Z**
- Source : https://github.com/Globox1997/FleshZ
- Licence : MIT

| Notre texture | Source | Traitement |
|---|---|---|
| `peau_epaisse` | `fleshz:item/hide` | Assombrie, contraste accentué |
| `peau_rare` | `fleshz:item/hide` | Éclaircie vers un nacré, liseré doré |

FleshZ est aussi **porté en 1.21.11** pour ce serveur — voir `libs/fleshz/` et le
fichier de licence qui l'accompagne. Le portage conserve la licence MIT et son
auteur d'origine ; nos modifications sont décrites dans son propre README.

---

## Idées et textures de gadgets reprises

Les gadgets de l'Ingénieur (étape 10) reprennent les **idées** de deux mods ;
leur **code** est entièrement réécrit en Java pour Fabric 1.21.11, dans notre
espace de noms — aucune classe, aucun modèle de données, aucune recette n'est
copiée. Les textures de Gadgets Against Grind sont reprises telles quelles ;
celles de MBK's useful items ne le sont plus (voir ci-dessous).

### MBK's useful items — CC0-1.0

- Auteur : **MBK** (publié sous le pseudonyme du mod ; le manifeste dit « Me! »)
- Source : `mbks-useful-items-v2.1-fabric-1.21.10.jar`, licence CC0 1.0
  Universal jointe au jar (`LICENSE_mbks-useful-items`)
- Licence : CC0-1.0 — domaine public, aucune condition. La mention est faite par
  courtoisie.

Vingt-cinq idées de gadgets en viennent : aimant à butin, balise de rappel,
grappin (`rope_launcher`), les trois paires de lunettes, détecteur de minerai,
chercheur de géode, sonde à écho (`echo_locator`), résonateur de cristal, casque
de mineur, lanterne de sac (`backpack_lantern`), l'exosquelette (4 pièces),
bottes stabilisatrices, bottes de lave (`lava_walker_boots`), gants de portée
(`block_reach_gloves`), drone minier, kits de campement, de secours
(`dungeon_recovery_kit`) et de raffinage, compresseur de poche, broyeur
automatique (`auto_trash_mine`). **Leurs textures 32×32 ne sont pas reprises pour
les objets** : les vingt-cinq icônes du mod sont des dessins originaux en 16×16
(`tools/textures/MakeGadgets.java`), plus proches du pixel-art du jeu. Une seule
l'est, telle quelle : `mining_drone.png`, devenue `textures/item/drone_en_vol.png`,
le sprite du drone **en vol** — le mod d'origine le dessine ainsi, face à la caméra,
et c'est ce rendu qui est gardé.

Non repris, parce qu'ils cassent ou posent des blocs : `blast_pickaxe`,
`bedrock_drill_prototype`, `builder_wand`, `auto_torch`,
`portable_crafting_tablet`.

### Gadgets Against Grind — MIT

- Auteur : **MaxNeedsSnacks**
- Source : https://github.com/MaxNeedsSnacks/gag
- Licence : MIT, déclarée dans le manifeste du jar (`neoforge.mods.toml`,
  `license = "MIT"`) ; le texte de la licence est dans le dépôt, pas dans le jar

| Notre texture | Source (`gag:item/…`) |
|---|---|
| `pierre_de_foyer` | `hearthstone` |
| `pierre_de_foyer_amelioree` (+ `.mcmeta`) | `energized_hearthstone` (+ animation) |
| `corde_d_evasion` | `escape_rope` |
| `sel_sacre` | `sacred_salt` |
| `onguent_sacre` | `sacred_salve` |
| `baume_sacre` | `sacred_balm` |
| icône d'effet `mob_effect/repoussant` | `mob_effect/repelling` |

La liaison de la pierre par la foudre, chez GAG, est remplacée par
l'aubergiste ; les dynamites, le sablier et l'outil d'étiquetage ne sont pas
repris.

Licence MIT, reproduite au nom de son auteur :

```
MIT License

Copyright (c) MaxNeedsSnacks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Chaîne textile reprise — Weaver's Paradise (MIT)

- Auteur : **Vortianski**
- Source : `weaversparadise-1.6.2.1.jar` (NeoForge 1.21.1), licence MIT déclarée
  dans `META-INF/neoforge.mods.toml`
- Licence : MIT

Le Couturier (étape 11) reprend la **chaîne de ressources** de Weaver's
Paradise — coton, fibre, bobines, tissus, aiguilles, bases de haut, manches,
jambes de pantalon, et leurs proportions — réécrite en Java pour Fabric
1.21.11 dans notre espace de noms ; aucune classe n'est copiée. Vingt-six
**icônes d'objet** et deux **textures de bloc** sont reprises telles quelles :

| Nos textures | Source (`weaversparadise:…`) |
|---|---|
| `item/coton` · `fibre_de_coton` | `item/cotton_boll` · `raw_cotton` |
| `item/bobine_vide` · `bobine_de_coton` · `bobine_de_laine` · `bobine_de_jean` · `bobine_de_soie` | `item/empty_cotton_spool` · `cotton_spool` · `wool_spool` · `jeans_spool` · `silk_spool` |
| `item/tissu_de_coton` · `tissu_de_laine` · `tissu_de_jean` · `tissu_de_soie` | `item/cotton_cloth` · `wool_cloth` · `jeans_cloth` · `silk_cloth` |
| `item/aiguille` · `aiguille_enfilee` | `item/needle` · `needle_with_thread` |
| `item/base_de_haut_en_coton` · `_laine` · `_soie` | `item/cotton_upperwear_base` · `wool_…` · `silk_…` |
| `item/manche_courte_de_*` · `manche_longue_de_*` (coton, laine, soie) | `item/*_short_sleeve` · `*_long_sleeve` |
| `item/jambe_de_coton` · `jambe_de_laine` · `jambe_de_jean` · `jambe_de_soie` | `item/cotton_pant` · `wool_pant_leg` · `jeans_pant` · `silk_pant` |
| `block/cotonnier` · `block/cotonnier_jeune` | `block/cotton_plant_6` · `block/cotton_plant_2` |

Les treize icônes de vêtements (débardeur, chemises, pull, pantalons, bas,
bonnet, capes) sont dessinées pour ce mod (`tools/textures/MakeTextile.java`) :
Weaver's rendait ses vêtements par un modèle en couches, sans icône. Ce rendu
n'est pas repris.

Licence MIT, reproduite au nom de son auteur :

```
MIT License

Copyright (c) Vortianski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Textures originales

`tendon`, `ecailles_preparees`, `cuir_exotique`, `cuir_rare`, la baguette
`outil_node`, les vingt-cinq icônes de gadgets de l'Ingénieur (aimant,
balise, grappin, lunettes, détecteurs, casque, lanterne, exosquelette, bottes,
gants, drone, kits, compresseur, broyeur), les treize icônes de vêtements du
Couturier et l'icône de l'effet « Repas » sont dessinées pour ce mod
(`tools/textures/`). Aucun emprunt.

---

## Mods référencés sans être modifiés ni redistribués

Ces mods sont cités par leurs identifiants dans nos tags et nos données. Aucun
de leurs fichiers n'est copié ici, et leurs objets restent optionnels : les tags
sont écrits en `"required": false`, donc le serveur démarre et les tags se
chargent même si le mod est absent.

| Mod | Ce qu'on lui emprunte |
|---|---|
| Farmer's Delight Refabricated (MIT, © 2020 vectorwing ; portage MehVahdJukaar, ChrysanthCow, cassiancc) | Ses plats et ses familles d'ingrédients, cités dans les recettes du Cuisinier ; quatorze de ses recettes de marmite remplacées par un datapack intégré — rien n'est copié |
| jewelry, mmo_accessories, Lands of Icaria, Hazen's Stuff, Wizards, Armory, Bards, Elemental Wizards, Forcemaster, Death Knights, Runes, Accents | Leurs bijoux, gemmes, robes, chapeaux, sacoches et plats, cités dans les recettes du Joaillier, du Couturier et du Cuisinier |
| Easy NPC (MIT, Markus Bordihn) | Son registre d'actions publiques — voir README |
| Farmer's Delight | `farmersdelight:tree_bark`, l'agent tannant |
| More RPG Classes | `wolf_fur`, `polar_bear_fur`, acceptées comme fourrures équivalentes |
| Lands of Icaria | `myrmeke_scales`, `slug_scales`, `aeternae_hide` |
| Hazen N Stuff | `shadow_scale` |
| HMobs | `brown_bear_hide` |
| Minecraft | `rabbit_hide`, `leather` |
