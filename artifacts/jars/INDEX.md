# Bibliothèque JAR — Indivis

Les fichiers de ce dossier sont de **vrais JAR archivés avec Git LFS**. Le fichier Git visible est un pointeur LFS contenant le SHA-256 et la taille du binaire ; le contenu complet est stocké dans Git LFS et récupéré lors d'un checkout LFS.

## Première vague importée

### Systèmes centraux
- `nexuscharacters/NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.1-LEGACY-SKIN-COMPAT.jar`
- `capitale-rp-hud/capitale_rp_hud_BETA_1_3_2_NEXUS_AUTHORITY_FINAL_ARMOR40.jar`
- `capitale-skills-items/capitale_skills_items_fabric_1_7_18_EVENT_ACCESS_FIX_1_21_11.jar`
- `haute-capitale-rpg/haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC2.jar`

### Stack spells / combat
- `spell-engine/spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC7-HAZENN-FULLCOMPAT.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC7-DIRECT-RESIST.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC8-CLASSFORMAT-FIX.jar`
- `hazennstuff/hazennstuff-fabric-1.21.11-1.0.0-b4-HC-SPELLCOMPAT1.jar`
- `arsenal/arsenal-fabric-1.5.1.002-mmo+1.21.11-HC-FR-PRIMARY-ORDER1.jar`
- `azurelibarmor/azurelibarmor-fabric-1.21.11-3.1.4-HC-TEST3-RC1.jar`
- `witcher-class/witcher-class-mod-fabric-3.1.0-1.21.11-mmo-HC-FOOTWORK-TEST3-RC1.jar`

### Modules Haute Capitale
- `capitale-admin-commands/capitale_admin_commands_1_0_2_SERVER_ONLY_EVENT_ACCESS_FIX.jar`
- `capitale-armor-test/capitale_armor_test-0.7.3-local-player-first-person-only.jar`
- `capitale-currency/capitale_currency_BETA_1_0_hotfix5_bourse50_stack99_legacy_style_1_21_11.jar`
- `capitale-housing/capitale_housing_BETA_0_1_0_HOTFIX2_fabric_1_21_11.jar`
- `capitale-weapons/capitale_weapons_standalone-0.5.0-gameplay-stats.jar`
- `haute-capitale-fusils/haute_capitale_fusils-0.1.0+1.21.11.b3.jar`

## Politique

- Plusieurs **versions différentes** d'un même module sont conservées : c'est l'historique.
- Deux fichiers ayant exactement le même SHA-256 ne doivent pas être archivés deux fois sous des noms différents.
- Les suffixes locaux Windows comme `(1)`, `(2)` ou `(3)` ne constituent pas une nouvelle version.
- Une build n'est pas automatiquement considérée stable parce qu'elle se trouve dans cette bibliothèque.
- `server-manifest.yml` indique ce qui est déployé/candidat ; cette bibliothèque indique ce qui a été conservé.

Le workflow `Check JAR library` exécute `tools/check_jar_duplicates.py` sur chaque modification de la bibliothèque.
