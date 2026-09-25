# Capitale Creatures — Biomes BETA 0.17

Minecraft 1.21.11 / Fabric.


## BETA 0.17 — Variantes par famille + première passe du pool agressif

Cette passe ne rééquilibre **que le pool agressif**. Le pool passif est volontairement reporté.

### Poids par famille

Les variantes de couleur/sexe Dino Mounts ne cumulent plus artificiellement leur fréquence. Chaque règle multi-variante possède désormais un `family_weight` unique. Le bundle 1.2.23 utilise une échelle interne de 12 et partage ce budget entre les variantes de la règle.

Exemple : quatre Triceratops avec `family_weight: 1` représentent ensemble le même poids logique qu'une seule espèce weight 1, au lieu d'un poids cumulé 4. Toutes les variantes restent disponibles.

### Petits Chaos

Valeurs réelles du mod avant correction :
- `chaos_mmo_ai:maggot` : 12 HP / 3 dégâts ; spawn 18, groupe 2–5 ;
- `chaos_mmo_ai:babyspider` : 18 HP / 3,5 dégâts ; spawn 18, groupe 2–5 ;
- `chaos_mmo_ai:corpsefly` : 34 HP / 5 dégâts ; spawn 10, groupe 1–3.

BETA 0.17 applique :
- Maggot : **6 HP**, weight **5**, groupe **1–2** ;
- Baby Spider : **8 HP**, weight **5**, groupe **1–2** ;
- Corpse Fly : **6 HP**, weight **3**, groupe **1–2**.

Les dégâts ne sont pas encore modifiés dans cette build : ils font l'objet de la prochaine décision d'équilibrage.

### Catégories agressives

`hmobs:brown_bear`, `hmobs:lion` et `hmobs:hyena` passent dans le spawn group `MONSTER`, leur IA pouvant cibler les joueurs. Le zèbre reste `CREATURE`.

Le remapping culturel de BETA 0.16 est conservé tel quel ; aucune nouvelle espèce non intégrée n'est encore injectée dans les tables naturelles.


## BETA 0.16 — Remapping du spawnpool sur les biomes culturels

Cette passe ne modifie **ni les weights ni les tailles de groupe**. Elle remappe d'abord les habitats du spawnpool existant sur la géographie culturelle validée afin de tester séparément la répartition et la densité.

### Tags culturels

- `#capitale_creatures:culture_empire` : `plains`, `forest`, `taiga`, `jungle`, `windswept_gravelly_hills`, `snowy_slopes`.
- `#capitale_creatures:culture_elves` : `sunflower_plains`, `cherry_grove`, `meadow`.
- `#capitale_creatures:culture_nordics` : `snowy_plains`, `snowy_taiga`, `ice_spikes`.
- `#capitale_creatures:culture_dwarves` : `windswept_hills`, `grove`, `stony_peaks`, `jagged_peaks`.
- `#capitale_creatures:culture_orcs_provisional` : `desert` + variantes de `badlands` ; palette provisoire jusqu'au verrouillage du territoire orc.
- `#capitale_creatures:culture_corruption` : `capitale:donjon` + `indivis:corruption`.

### Effets sur les habitats existants

- les forêts tempérées deviennent `forest` (Empire) + `cherry_grove` (Elfes) ;
- les forêts générales deviennent `forest`, `taiga`, `cherry_grove`, `snowy_taiga`, `grove` ;
- la jungle du pool devient uniquement `minecraft:jungle`, conforme au biome impérial de frontière ;
- les pools froids sont limités aux biomes culturels froids terrestres et n'incluent plus rivières/océans gelés ;
- les règles historiques « partout » (chouette, ours noir, aigle, escargot) utilisent désormais `#capitale_creatures:cultural_surface_land` au lieu de `all_overworld`, donc plus de spawn dans océans, grottes, Deep Dark, villes ou corruption ;
- les choix historiques sont conservés, notamment le fennec en forêt, les variantes de dinos exclues et les répartitions jungle/désert/badlands.

Les villes restent entièrement sans spawn naturel. Aucun nouveau mob Lands of Icaria, Myths of the Sea, Mythika, etc. n'est ajouté dans cette passe.

## BETA 0.15 — Cinq nouvelles villes impériales safe

Ajoute à la protection urbaine :
- `indivis:blanche_fleche` ;
- `indivis:port_levant` ;
- `indivis:sillons_d_or` ;
- `indivis:sombrefleche` ;
- `indivis:clos_des_ormes`.

Ces cinq biomes rejoignent `#capitale_creatures:city_no_spawn` et `exclude_biomes` des patrouilles orques. Avec le bundle 1.2.22, ils sont également ciblés par RegistryKey exact dans le `clearSpawns()` POST_PROCESSING et par le garde-fou dur des patrouilles.

Leur JSON dans `capitale_core 1.5.6-RC9AR` déclare en plus les huit catégories de spawners vides.

## BETA 0.14 — Villes sans spawn naturel

Ajoute `#capitale_creatures:city_no_spawn` pour la Haute Capitale et les huit biomes urbains `indivis:*` actuels.

Le bundle 1.2.18 utilise ce tag dans le sélecteur de son `clearSpawns()` en phase `POST_PROCESSING`. Les villes sont donc nettoyées des entrées de spawn naturel ajoutées par Minecraft ou par les mods, sans être ajoutées à `#capitale_creatures:all_overworld` et sans recevoir ensuite la faune de l'Overworld.

La protection des patrouilles orques reste séparée et est conservée depuis le bundle 1.2.17.

`capitale:donjon` et `indivis:corruption` ne sont volontairement pas inclus dans ce tag.

## BETA 0.13 — Patrouilles orques sécurisées par biome

Cette version conserve l'équilibrage de BETA 0.12 et corrige le filtrage des patrouilles.

Les patrouilles sont maintenant explicitement exclues de :
- `capitale:capitale` ;
- `capitale:donjon` ;
- `indivis:corruption` ;
- `indivis:lion_port` ;
- `indivis:clairval` ;
- `indivis:haute_rive` ;
- `indivis:ilystara` ;
- `indivis:sylvarhen` ;
- `indivis:avaleiv` ;
- `indivis:skarnfjord` ;
- `indivis:durak_vor` ;
- toutes les rivières vanilla ;
- tous les océans et océans profonds vanilla.

Le contrôleur du bundle vérifie `exclude_biomes` au biome du joueur, au point d'ancrage de la patrouille et à la position exacte de chaque membre. Cette correction doit donc être déployée avec le datapack créatures ; le seul remplacement de `capitale-core` ne modifie pas la configuration lue par ce contrôleur.

## BETA 0.12 — Orcs plus présents

Les autres règles de BETA 0.11 sont conservées. Cette version augmente uniquement la présence des orcs.

### Donjon `capitale:donjon`

- plafond local : **24 orcs** dans un rayon de **64 blocs** (contre 16 auparavant) ;
- contrôle toutes les 2 secondes ;
- orcs nommés préservés ;
- flag de persistance retiré aux orcs de combat non nommés afin qu'ils puissent despawn normalement ;
- nettoyage des excédents au-dessus du plafond.

Le but est de garder une population dense et dangereuse sans revenir aux centaines d'orcs observées avant la mise en place du plafond.

### Patrouilles d'orcs

Composition inchangée :
- 2 `autonomous_orc_mobs:orc_archer`
- 3 `autonomous_orc_mobs:orc_warrior`
- 2 `autonomous_orc_mobs:female_orc_warrior`
- 1 `autonomous_orc_mobs:orc_warlock`

Fréquence renforcée :
- vérification toutes les **45 secondes** ;
- **20 %** de chance par vérification ;
- cooldown de **6 minutes** après une patrouille réussie ;
- plafond de **16 orcs de patrouille** dans un rayon de 96 blocs.

En conditions éligibles, cela donne environ une patrouille toutes les **9–10 minutes en moyenne** après prise en compte du cooldown, contre environ 20 minutes auparavant. Deux groupes peuvent ponctuellement coexister sans permettre une accumulation illimitée.

Les patrouilles restent :
- en surface uniquement ;
- hors de l'eau ;
- exclues des biomes urbains, de `capitale:donjon`, de `indivis:corruption`, des rivières et des océans ;
- limitées aux chunks déjà chargés.

## Architecture conservée

- `biome_assignments.json` : spawns naturels terrestres, Thornshell et requin.
- `aquatic_runtime_rules.json` : nageurs stricts.
- `spawn_profiles_active.json` : poids et tailles de groupe.
- `disabled_natural_dino_variants.json` : variantes désactivées.
- `thornshell_behavior_rules.json` : comportement benthique/amphibie.
- `orc_dungeon_rules.json` : plafond du donjon.
- `orc_patrol_rules.json` : fréquence/composition des patrouilles.
