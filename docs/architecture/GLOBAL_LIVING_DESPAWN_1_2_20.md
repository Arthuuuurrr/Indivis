# Capitale Creatures 1.2.20 — Global LivingEntity hard despawn

## Objectif

Le hard-despawn à plus de 128 blocs ne doit plus être limité à quelques mobs du bundle.

À chaque passage du contrôleur (toutes les 20 ticks), toute entité vivante éligible située à plus de 128 blocs du joueur le plus proche est supprimée.

La sélection repose désormais sur `LivingEntity` (`class_1309`) et non sur une liste fermée d'IDs.

## Exemptions

Ne sont pas supprimés :

- joueurs serveur ;
- `minecraft:armor_stand` ;
- toutes les entités dont l'ID commence par `easy_npc:` ;
- toute LivingEntity ayant un nom personnalisé ;
- toute entité implémentant `Tameable` et ayant réellement un propriétaire ;
- toute entité portant le command tag `indivis_no_hard_despawn` ;
- variantes Dino Mounts non `*_hostile` (montures/acquisition) ;
- namespace `autonomous_colored_horses:` par sécurité vis-à-vis des montures ;
- villageois, wandering traders, iron/snow golems, allays et happy ghasts.

## Important : PersistenceRequired

`PersistenceRequired` n'est volontairement PAS une exemption générale.

Motif : certains mobs moddées peuvent être marqués persistants par leur implémentation alors qu'ils appartiennent malgré tout à la population sauvage. C'est précisément ce qui risquerait de laisser des `chaos_mmo_ai:maggot` et autres mobs présents indéfiniment.

## Opt-out universel

Pour préserver explicitement une entité particulière :

```mcfunction
/tag <entité> add indivis_no_hard_despawn
```

Cela permet aux quêtes, boss ou scripts futurs de protéger une entité sans nouveau patch Java.

## Validation du binaire

Artifact :
`capitale_creatures_bundle-1.2.20-fabric-1.21.11-GLOBAL-LIVING-DESPAWN.jar`

SHA-256 :
`05e7d51ff972fe97a0d547d64fda3a6f8fe56d33ee39659c282f2826bf584930`

Contrôles :
- ZIP/JAR integrity : PASS ;
- ASM BasicVerifier : PASS sur Runtime124, OrcPatrol1212 et DatapackSpawnController ;
- 11/11 nested fauna JARs byte-identical à 1.2.19 ;
- seuls `CapitaleCreaturesRuntime124.class` et `fabric.mod.json` changent parmi les entrées existantes ;
- ajout de `META-INF/INDIVIS_GLOBAL_LIVING_DESPAWN_1_2_20.txt`.
