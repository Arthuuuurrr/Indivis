# Bibliothèque JAR — Indivis

Les fichiers de ce dossier sont de **vrais JAR archivés avec Git LFS**. Le fichier Git visible est un pointeur LFS contenant le SHA-256 et la taille du binaire ; le contenu complet est stocké dans Git LFS et récupéré lors d'un checkout LFS.

## Builds archivés

### Systèmes centraux
- `nexuscharacters/NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.1-LEGACY-SKIN-COMPAT.jar`
- `capitale-rp-hud/capitale_rp_hud_BETA_1_2_9_NEXUS_AUTHORITY_1_21_11.jar`
- `capitale-rp-hud/capitale_rp_hud_BETA_1_3_1_ARMOR40_NEXUS_AUTHORITY_FINAL_1_21_11.jar`
- `capitale-rp-hud/capitale_rp_hud_BETA_1_3_2_NEXUS_AUTHORITY_FINAL_ARMOR40.jar`
- `capitale-skills-items/capitale_skills_items_fabric_1_7_18_EVENT_ACCESS_FIX_1_21_11.jar`
- `haute-capitale-rpg/haute-capitale-rpg-0.3.0+1.21.11.b2.jar`
- `haute-capitale-rpg/haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC1.jar`
- `haute-capitale-rpg/haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC2.jar`

### Stack spells / combat
- `spell-engine/spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC7-HAZENN-FULLCOMPAT.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-BRIDGE1.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC1.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC2.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC6.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC7-DIRECT-RESIST.jar`
- `spell-power/spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC8-CLASSFORMAT-FIX.jar`
- `hazennstuff/hazennstuff-fabric-1.21.11-1.0.0-b4-HC-SPELLCOMPAT1.jar`
- `arsenal/arsenal-fabric-1.5.1.002-mmo+1.21.11-HC-FR-PRIMARY-ORDER1.jar`
- `azurelibarmor/azurelibarmor-fabric-1.21.11-3.1.4-HC-ALL-FIXES.jar`
- `azurelibarmor/azurelibarmor-fabric-1.21.11-3.1.4-HC-TEST3-RC1.jar`
- `witcher-class/witcher-class-mod-fabric-3.1.0-1.21.11-mmo-HC-FOOTWORK-TEST3-RC1.jar`

### Modules Haute Capitale
- `capitale-admin-commands/capitale_admin_commands_1_0_2_SERVER_ONLY_EVENT_ACCESS_FIX.jar`
- `capitale-armor-test/capitale_armor_test-0.7.3-local-player-first-person-only.jar`
- `capitale-housing/capitale_housing_BETA_0_1_0_HOTFIX2_fabric_1_21_11.jar`
- `haute-capitale-fusils/haute_capitale_fusils-0.1.0+1.21.11.b3.jar`

## Historique — vague 1

### Spell Power
- base upstream/locale — SHA-256 `a62e76a4c9ec096261b390c171868380198e8895d91ca1c17d2d9ba9f4469a00`
- HAZENN BRIDGE1 — SHA-256 `3baa77f8822585c5bf8f824f14a003779b0fff58f8964455b9773f326a4e9d8c`
- TEST3 RC1 — SHA-256 `ba0c66e31eebcb04bd1d0d167782d1f8f3737b6e347d9fa999a414139b3f63fe`
- TEST3 RC2 — SHA-256 `fe31701f8db9a2822906fc49d83fd5a9e3f63ea439221d9b26dc787e81c112d8`
- TEST3 RC6 — SHA-256 `f05f2bf01e6b55818dd24eec290cdeeb8e8fcd3a1bdd6c4c4047641fef1eba71`
- TEST3 RC7 Direct Resist — déjà présent
- TEST3 RC8 ClassFormat Fix — déjà présent

### HUD
- 1.2.9 Nexus Authority — SHA-256 `dcb51a9a715096477476ea736389b26e00745a0eb26ccd208b5745f3913d4ba8`
- 1.3.1 Armor40 Nexus Authority Final — SHA-256 `fdf82737c7c541bf0b40251cfb9e5a503c1f911d8070f3c8900420f206e23fae`
- 1.3.2 Nexus Authority Final Armor40 — déjà présent

### Haute Capitale RPG
- b2 base — SHA-256 `b149462ea2deb98f5ffd06a759be847e223bc0a77d6ee9e51b82a5dd7625ddc3`
- TEST3 RC1 — SHA-256 `36e1e6c3fc8f7d37d77f7a0d47f1b6feb7715d87604057a47ff39d8b21e276ec`
- TEST3 RC2 — déjà présent

### AzureLibArmor
- HC ALL FIXES — SHA-256 `593a71bc2449cd488b40867fdbe407864b2750408f9cac667c67a91ff823aad4`
- TEST3 RC1 — déjà présent

## Politique

- Plusieurs **versions différentes** d'un même module sont conservées : c'est l'historique.
- Deux fichiers ayant exactement le même SHA-256 ne doivent pas être archivés deux fois sous des noms différents.
- Les suffixes locaux Windows comme `(1)`, `(2)` ou `(3)` ne constituent pas une nouvelle version.
- Une build n'est pas automatiquement considérée stable parce qu'elle se trouve dans cette bibliothèque.
- `server-manifest.yml` indique ce qui est déployé/candidat ; cette bibliothèque indique ce qui a été conservé.

Le workflow `Check JAR library` exécute `tools/check_jar_duplicates.py` sur chaque modification de la bibliothèque.

## À réimporter

- `capitale_currency ... hotfix5` : premier transfert détecté invalide, source réelle retrouvée.
- `capitale_weapons_standalone 0.5.0` : premier transfert détecté invalide, source réelle à réinjecter.
