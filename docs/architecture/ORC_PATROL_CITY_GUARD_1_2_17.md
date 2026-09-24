# Capitale Creatures Bundle 1.2.17 — City-safe Orc Patrols

## Cause du correctif

Le contrôleur de patrouilles `CapitaleCreaturesOrcPatrol1212` sait lire `exclude_biomes` et contrôle le biome du joueur, le point d'ancrage et la position finale de chaque membre.

Le point fragile de 1.2.16 est la sélection de la source de configuration : le contrôleur cherche les datapacks du monde contenant `data/capitale_creatures/spawn_rules/orc_patrol_rules.json`, les trie selon la date de modification du fichier/dossier source et choisit le plus récent. Si la source externe attendue n'est pas celle retenue, il peut charger une ancienne configuration ; sa configuration embarquée 1.2.16 n'excluait que `capitale:donjon`.

## Correctif 1.2.17

La 1.2.17 ajoute un garde-fou directement dans le bytecode du contrôleur. Avant toute comparaison avec `exclude_biomes`, les biomes protégés sont normalisés vers `capitale:donjon`, valeur déjà exclue par toutes les configurations historiques connues.

Biomes protégés :
- `capitale:capitale`
- `capitale:donjon`
- `indivis:corruption`
- `indivis:lion_port`
- `indivis:clairval`
- `indivis:haute_rive`
- `indivis:ilystara`
- `indivis:sylvarhen`
- `indivis:avaleiv`
- `indivis:skarnfjord`
- `indivis:durak_vor`
- rivières vanilla
- océans et océans profonds vanilla

Le JSON embarqué du JAR est également synchronisé avec ces exclusions.

## Vérifications effectuées

- SHA-256 du JAR livré : `9c2837193ee3cabfd81d41ff8d1f49e4aec1f70a8a557d3383097ecf41720c94`
- archive JAR validée par `zip -T`
- 11 JARs imbriqués comparés à 1.2.16 : octets identiques
- seuls la classe de patrouilles, `orc_patrol_rules.json` et `fabric.mod.json` changent, plus une note META-INF
- classe patchée chargée avec `-Xverify:all`
- tests du normaliseur :
  - `indivis:lion_port -> capitale:donjon`
  - `indivis:durak_vor -> capitale:donjon`
  - `capitale:capitale -> capitale:donjon`
  - `minecraft:river -> capitale:donjon`
  - `minecraft:plains -> minecraft:plains`
  - `minecraft:forest -> minecraft:forest`

## Déploiement

Remplacer `capitale_creatures_bundle-1.2.16-fabric-1.21.11-HMOBS_ORC_PATROLS_DUNGEON-7MOBS.jar` par la 1.2.17. Ne pas conserver les deux versions simultanément. Redémarrage complet requis.
