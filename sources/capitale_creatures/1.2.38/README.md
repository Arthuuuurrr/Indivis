# Capitale Creatures 1.2.38 — marine / spawn safety

Base runtime : `capitale_creatures_bundle 1.2.37`.
Datapack associé : `capitale_creatures_biomes_BETA_0_27`.
Issue : #121 (suite des constats #119 et #120).

## IA marine
- nage idle 3D pour Aegirocassis, Dunkleosteus, Corpsefish, Gluttonfish et Mosasaurus hostiles ;
- Gluttonfish privilégie explicitement le bas de la colonne d'eau ;
- Mosasaurus : fallback de poursuite des joueurs dans l'eau à 28 blocs ;
- couleur 4 hostile reconnue par l'IA si elle existe déjà, mais reste exclue du spawn naturel ;
- `MarineCombat130` reste prioritaire pour les autres espèces en combat ; Mosasaurus reçoit un fallback explicite de poursuite même lorsqu’une cible existe, afin de couvrir aussi la couleur 4 et les cas où son IA native reste statique.

## Sécurité des spawns
- les prédicats existants sont conservés puis renforcés ;
- position courante sèche ;
- support sous les pieds sec ;
- support non `#minecraft:leaves` ;
- support `isSolidBlock` ;
- la même validation est injectée dans `OrcPatrol1212.surfaceAt`, donc chaque membre de patrouille est revalidé.

## Océans profonds uniquement
Les 4 biomes concernés : deep_ocean, deep_lukewarm_ocean, deep_cold_ocean, deep_frozen_ocean.
- plafond runtime : 16 -> 24 autour d'un joueur dans un océan profond ;
- bonus vérifié toutes les 40 ticks ;
- chaque colonne candidate est revalidée comme biome profond ;
- poids profond : Mosasaurus 2 -> 3/variante, Gluttonfish 2 -> 4 ;
- bonus additifs profonds : Aegirocassis +1/variante, Dunkleosteus +2/variante, Corpsefish +4 ;
- règles des océans classiques et rivières de BETA 0.26 inchangées.

## Validation statique
- ASM BasicVerifier PASS sur les 3 nouvelles classes et OrcPatrol patché ;
- aucun JAR imbriqué modifié ;
- bundle 1.2.37 -> 1.2.38 : 2 entrées existantes modifiées (`fabric.mod.json`, `OrcPatrol1212.class`), 5 entrées ajoutées, 0 supprimée ;
- BETA 0.26 -> 0.27 : config globale inchangée ; règles Aegiro/Dunkle/Piranha/Corpsefish normales inchangées ; seules les règles deep-only Mosa/Glutton sont modifiées et 3 bonus deep-only ajoutés ;
- tous les JSON valides ; archives JAR/ZIP valides.

SHA-256 bundle : `7fa71bdcbdff7a7844274a1dfbaa4f341b147ec7527b6846cc2f0ff005dffdd2`
SHA-256 datapack : `fa74dda1ea35a4950b2f7ebc8d0a5115e80f13993cb58512374febeadf287884`

Runtime à valider avant fusion.
