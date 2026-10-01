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
| capitale_creatures_bundle | **1.2.16** (manifeste de `main`) — 1.2.17 confirmé par log serveur du 24/09 selon #100 (non fusionnée) | base 1.2.16 construite par Clément ; chaîne de patchs 1.2.27–1.2.32 sur `main`, suite dans #117 et #122 ; sources complètes du bundle encore à importer | #61, #72, #100 |
| Spell Engine HC | TEST3 RC7 | binaire hashé | #53 |
| Spell Power HC | **serveur RC6 / client RC8 CLASSFORMAT FIX** | RC8 candidat hashé | #70 |
| Haute Capitale RPG | TEST3 **RC2** = b2 + patchs **binaires** RC1–RC2 | RC1/RC2 : **aucune source** (liste des classes dans `docs/spell-integration/PATCH_INVENTORY.md`) ; RC3 FIREARMS-SPELLBAR : patch source dans #92 ; b3/b4 (caméra RPG + classe nécromancien) : sources dans #104, **lignée parallèle sans RC1–RC3 — ne pas déployer telle quelle** | #53, #105 |
| Hazennstuff HC | SPELLCOMPAT1 | binaire hashé | #53 |
| Arsenal HC | PRIMARY ORDER1 | binaire hashé | #5, #53 |
| Witcher Class HC | Footwork RC1 | binaire hashé | #53 |
| capitale_heraldry | **0.1.7** | source exacte 0.1.7 encore à importer | #72 |
| MMO Music Zones | **1.2.7 indivis-dungeon-biome-FULL** | source exacte à importer | #60, #72 |
| Dialogue/caméra NPC (`haute_capitale_dialogue`) | **b9** (serveur, log du 01/10) | b7 dans #104 ; b8 backup ; **b10 DATAPACK-COMMAND-HOTFIX** construit le 01/10 depuis b9 (SHA-256 `a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057`) ; correctif source complet b9 à importer | #59, #104, #72 |
| Journal de quêtes (`haute_capitale_quetes`) | serveur b3 / client b2 (relevé du 22/09) | b5 construit (système de montures, #30) ; sources à importer | #30, #72 |
| Haute Capitale Pirates | serveur b4 / client b2 (relevé du 22/09) | b4 construit ; sources à importer | #72 |
| Moteur d'instances `dungeonz` | non déployé | b23 construit, source à importer (listé `dungeon2-hc` dans #72) | #32, #72 |
| Datapack `hc_quetes` | **b8** (relevé du 22/09) | b17 = dernier zip correct ; le zip b18 du 30/09 est illisible par Minecraft (chemins `\`), à refaire ; sources à importer | #33, #72, #108, #109, #111, #115 |

Nom : le journal de quêtes s'appelait par erreur « Haute Capitale Quests » / `haute-capitale-quests-*.jar`.
Son identifiant réel est `haute_capitale_quetes` (fichiers `haute-capitale-quetes-*.jar`), vérifié dans le JAR b3.

Modules de Clément ajoutés depuis le 26/09, chacun suivi dans une issue « à tester » (sources à importer, #72) :
`hurans` #140, `goblins_tyranny` #141, `tral` #142, `legendaryshrines` #143, `ancient_remnants` #144,
`hmag` #145, `thalassophobia` #146, `mythicmounts` (ground edition) #30.

Voir `server-manifest.yml`, `docs/RUNTIME_SNAPSHOT_2026-09-22.md` et
`docs/CLEMENT_WORK_AUDIT.md` (relevé vérifié des modules de Clément).
