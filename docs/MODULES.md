# Registre des modules

Ce document indique où se trouve la source de vérité de chaque composant du projet.

| Module | Type | Version de référence | État Git | Suivi |
|---|---|---|---|---|
| capitale_core | Datapack | 1.5.6-RC9AO | import partiel (metadata/docs) | #57 |
| capitale_skills / CapSkills | Datapack | 0.10.19 RC2F | import partiel (metadata/notes) | #55, #53 |
| capitale_creatures_biomes | Datapack | BETA 0.12 | **import complet** | #38, #47 |
| NexusCharacters HC | Mod Fabric | 0.8.0-alpha1.1 | sources de patch/référence en cours d'import | #58 |
| capitale_rp_hud | Mod Fabric | 1.3.2 | binaire référencé par hash | #56 |
| capitale_skills_items | Mod Fabric | 1.7.18 | binaire référencé par hash | #53 |
| capitale_creatures_bundle | Mod Fabric | 1.2.14 | binaire référencé par hash | #61 |
| Spell Engine HC | Mod Fabric | TEST3 RC7 | build de test référencé | #53 |
| Spell Power HC | Mod Fabric | TEST3 RC7 Direct Resist | build de test référencé | #53 |
| Haute Capitale RPG | Mod Fabric | TEST3 RC2 | build de test référencé | #53 |
| Hazennstuff HC | Mod Fabric | SPELLCOMPAT1 | build de test référencé | #53 |
| Arsenal HC | Mod Fabric | FR PRIMARY ORDER1 | build de test référencé | #5, #53 |
| Witcher Class HC | Mod Fabric | Footwork RC1 | build de test référencé | #53 |
| AzureLibArmor HC | Mod Fabric | TEST3 RC1 | build de test référencé | #2, #49, #53 |
| capitale_heraldry | Mod Fabric | 0.1.7 connue | source courante à récupérer | à créer au besoin |
| MMO Music Zones | Mod Fabric | 1.2.7 connue | artefact courant à récupérer | #60 |
| Dialogue/caméra NPC | Mod Fabric | version Clément courante | source à récupérer | #59 |

## Statuts d'import

**Import complet** signifie que les fichiers exploitables directement sont présents dans Git et peuvent être diffés/reviewés.

**Import partiel** signifie qu'une version canonique est identifiée et vérifiable par SHA-256, mais que le snapshot complet doit encore être déplié dans Git.

**Binaire référencé par hash** signifie que le JAR est connu précisément mais n'est pas utilisé comme source de vérité du code.

Le fichier machine-readable correspondant est server-manifest.yml.

## Règle

Quand une nouvelle version est testée ou mise en production :
1. branch/PR ;
2. mise à jour des sources ;
3. mise à jour de server-manifest.yml ;
4. tests ;
5. merge ;
6. seulement ensuite déploiement serveur.
