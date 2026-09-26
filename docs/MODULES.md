# Registre des modules

La source de vérité est désormais séparée en deux notions :

- **déployé** : ce qui est effectivement visible sur le serveur/client ;
- **source de référence** : la dernière source récupérée/versionnée, qui peut être plus récente que la production.

| Module | Déployé confirmé | Source Git / référence | Suivi |
|---|---|---|---|
| capitale_core | dossier présent, version exacte à confirmer | 1.5.6-RC9AO partiel | #57, #72 |
| capitale_skills | dossier présent, version exacte à confirmer | 0.10.19 RC2F partiel | #53, #72 |
| capitale_creatures_biomes | **BETA 0.10** | BETA 0.12 complet, non confirmé en prod | inventaire runtime |
| NexusCharacters HC | 0.8.0-alpha1.1 | patch/source de référence importé | #58 |
| capitale_rp_hud | 1.3.2 | binaire hashé | #56 |
| capitale_skills_items | 1.7.18 | binaire hashé | #53 |
| capitale_creatures_bundle | **1.2.16** | source complète encore à importer | #61, #72 |
| Spell Engine HC | TEST3 RC7 | binaire hashé | #53 |
| Spell Power HC | **serveur RC6 / client RC8 CLASSFORMAT FIX** | RC8 candidat hashé | #70 |
| Haute Capitale RPG | TEST3 RC2 (**b2**) | **b3 (caméra dialogue) et b4 (classe necromancien) construits, non déployés** | #53, #72 |
| Hazennstuff HC | SPELLCOMPAT1 | binaire hashé | #53 |
| Arsenal HC | PRIMARY ORDER1 | binaire hashé | #5, #53 |
| Witcher Class HC | Footwork RC1 | binaire hashé | #53 |
| capitale_heraldry | **0.1.7** | source exacte 0.1.7 encore à importer | #72 |
| MMO Music Zones | **1.2.7 indivis-dungeon-biome-FULL** | source exacte à importer | #60, #72 |
| Dialogue/caméra NPC | **b7** | source exacte à importer | #59, #72 |
| Haute Capitale Quests | serveur b3 / client b2 | source à synchroniser | divergence runtime |
| Haute Capitale Pirates | serveur b4 / client b2 | source à synchroniser | divergence runtime |
| Moteur d'instances `dungeonz` | non déployé | b23 construit, source à importer (listé `dungeon2-hc` dans #72) | #32, #72 |
| Datapack `hc_quetes` | b8 recensé | **b10 construit**, écart de deux builds à l'import | #33, #72 |

Voir `server-manifest.yml`, `docs/RUNTIME_SNAPSHOT_2026-09-22.md` et
`docs/CLEMENT_WORK_AUDIT.md` (relevé des builds mesurés sur le poste de développement).
