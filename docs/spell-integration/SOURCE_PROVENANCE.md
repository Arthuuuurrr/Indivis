# Provenance des bases et artefacts

## Bases auditées disponibles pendant la séquence

- Spell Engine : `spell_engine-fabric-1.10.5.001+1.21.11`
- Spell Power : `spell_power-fabric-1.6.1.001+1.21.11`
- Hazennstuff : `hazennstuff-fabric-1.21.11-1.0.0.b4`
- AzureLibArmor : `azurelibarmor-fabric-1.21.11-3.1.4`
- Witcher : JAR HC-FR-NATIF1 comme référence immédiate
- Haute Capitale RPG : TEST3 RC1 comme référence immédiate de RC2
- Arsenal : HC-FR-PRIMARY-ORDER1
- capitale_skills_items : 1.7.18-event-access-fix

## Sources publiques consultées

Pour les comportements upstream qui nécessitaient confirmation, les sources
officielles Spell Engine / Spell Power ont été utilisées, notamment :

- SpellChoice et containers d’items ;
- `TargetHelper.targetFromRaycast` ;
- TinyConfig `sanitize(true)` et persistance ;
- cast `movement_speed` ;
- comportement des summoned entities et scaling d’attributs.

## Politique des artefacts

Le README du dépôt indique que les JAR distribuables doivent idéalement vivre
dans les Releases/artefacts et que les sources/tests restent la référence.

Cette archive conserve donc :

- nom exact de chaque binaire ;
- SHA-256 exact ;
- manifests Fabric finaux ;
- inventaire des classes modifiées ;
- scripts de validation ;
- historique de décisions.

Le bundle distribué pendant cette séquence est identifié par le SHA-256 :

`a4a65ac6075ae58cb8cebc2c676ece06f15ea3bf2a9ae70b286c177176a67eed`

pour :

`HAUTE_CAPITALE_FINAL_MODS_RC7B_HAZENN_CRASHFIX.zip`.
