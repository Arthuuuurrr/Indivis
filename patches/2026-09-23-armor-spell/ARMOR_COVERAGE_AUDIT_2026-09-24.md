# Audit de couverture Armor40 — 24/09/2026

## Statut

La paire candidate `capitale_armor_balance 0.1.5 + Spell Engine RC17` n'est **pas encore validée comme exhaustive**.

## Couverture prouvée par le runtime 0.1.2

Extraction des deux logs client/serveur du 23/09/2026 :
- 953 IDs d'items distincts vus par `[capitale_armor_balance] x2 ARMOR ...`;
- 23 namespaces ;
- client et serveur donnent le même ensemble.

Namespaces vus :
- archers (12)
- archers_expansion (36)
- armory_rpgs (32)
- bards_rpg (16)
- berserker_rpg (16)
- block_factorys_bosses (8)
- capitale_armor_test (136)
- death_knights (16)
- dungeonnowloading (4)
- elemental_wizards_rpg (40)
- forcemaster_rpg (16)
- haute_capitale_metiers (7)
- hazennstuff (334)
- landsoficaria (21)
- minecraft (41)
- nightreign_armor (28)
- paladins (24)
- remains_cave (3)
- rogues (24)
- rpg-minibosses (9)
- treasures_of_the_dead (18)
- witcher_rpg (84)
- wizards (28)

0.1.5 étant le code 0.1.2 restauré (hors version), ces 953 entrées sont la couverture attendue de la passe DefaultItemComponentEvents.

## Familles RPG Series couvertes génériquement par RC17

RC17 ne contient pas une liste d'IDs. Il modifie `net.spell_engine.rpg_series.item.Armor.attributesFrom(...)` et multiplie le `ArmorSetConfig.Piece.armor` au moment où GENERIC_ARMOR est construit.

Sources amont vérifiées avec appel à `Armor.register(...)` :
- archers
- archers_expansion
- armory_rpgs
- bards_rpg
- berserker_rpg
- elemental_wizards_rpg
- forcemaster_rpg
- paladins
- rogues
- witcher_rpg
- wizards

Tous leurs sets passant par cette fonction commune sont couverts sans whitelist d'IDs.

## Oubli confirmé : Myths of the Sea

Le mod `myths_of_the_sea 1.3.0` est chargé dans le runtime, mais aucun namespace `myths_of_the_sea` n'apparaît dans les 953 conversions de la 0.1.2.

La source 1.3.0 contient pourtant le set `BAKE_KUJIRA_ARMOR_SET`, construit comme ArmorItem standard, avec matériau :
- helmet = 2
- chestplate = 4
- leggings = 5
- boots = 2

IDs dérivés de l'enregistrement :
- myths_of_the_sea:bake_kujira_helmet
- myths_of_the_sea:bake_kujira_chestplate
- myths_of_the_sea:bake_kujira_leggings
- myths_of_the_sea:bake_kujira_boots

Ces quatre items doivent être considérés **non couverts jusqu'à correction/validation**.

## Death Knights : couverture non prouvée au niveau final

Le runtime 0.1.2 voit 16 IDs death_knights :
- initiate_armor_* (4)
- frozen_champion_armor_* (4)
- crimson_guard_armor_* (4)
- plaguebringer_armor_* (4)

Cependant le build utilisé est `death_knights-1.0.0+1.21.11.jar`, un port spécifique. L'ancien code public Death Knights utilise une API Spell Engine legacy + `ConfigurableAttributes`, qui peut remplacer les attributs de base comme Armory le faisait.

Le binaire exact 1.21.11 n'est pas actuellement archivé dans le Git/Library accessible ; il est donc impossible de prouver statiquement que RC17 intercepte son chemin final. Statut : **à vérifier runtime ou avec le JAR exact**.

## Mods chargés sans entrée ARMOR détectée

Des mods chargés comme `mythika`, `artifacts`, `relics_rpgs` ou `mmo_accessories` n'apparaissent pas dans les logs x2. Cela n'implique pas nécessairement un oubli : ils peuvent ne pas fournir d'armure GENERIC_ARMOR standard (accessoires, reliques, contenu sans armure). Aucun item d'armure concret n'a été identifié pour Mythika lors de cet audit.

## Conclusion

Ne pas promouvoir 0.1.5 + RC17 comme solution Armor40 exhaustive tant que :
1. Bake Kujira / Myths of the Sea n'est pas couvert ;
2. Death Knights 1.21.11 n'est pas confirmé sur sa valeur finale ;
3. un test runtime vérifie au moins un set RPG Series, un set standard moddé, vanilla, Bake Kujira et Death Knights.

La prochaine correction doit préférer un point final commun d'application d'attributs 1.21.11 si sa stabilité est confirmée, plutôt qu'ajouter une whitelist d'IDs.
