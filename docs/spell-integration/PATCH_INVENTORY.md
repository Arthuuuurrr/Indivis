# Inventaire des modifications binaires

Ce document consigne les différences observées entre les JAR de base/de référence et les versions TEST3 finales.

## Spell Engine — original → RC7

Modifiés :

- `fabric.mod.json`
- `net/spell_engine/config/HudConfig.class`
- `net/spell_engine/internals/SpellTriggers.class`
- `net/spell_engine/internals/container/SpellContainerSource.class`
- `net/spell_engine/internals/target/SpellTarget.class`
- `net/spell_engine/mixin/client/control/SpellCastingMovement.class`
- `spell_engine.mixins.json`

Ajoutés :

- `net/spell_engine/config/HcHudMigration.class`
- `net/spell_engine/hc/HcHazennCastingMovementBridge.class`
- `net/spell_engine/hc/HcHazennSummonDamageBridge.class`
- `net/spell_engine/internals/HcArsenalGate.class`
- `net/spell_engine/internals/HcIntegration.class`
- `net/spell_engine/mixin/entity/HcHazennSummonDamageMixin.class`

Supprimés : aucun.

## Spell Power — original → RC7 Direct Resist

Modifiés :

- `fabric.mod.json`
- `net/spell_power/api/SpellResistance.class`
- `net/spell_power/api/SpellSchool.class`
- `spell_power.mixins.json`

Ajoutés :

- `net/spell_power/hc/HcHazennResistanceBridge.class`
- `net/spell_power/hc/HcHazennSpellPowerBridge.class`

Supprimé de la lignée RC6 :

- `net/spell_power/mixin/HcHazennSpellResistanceMixin.class`

Le branchement de résistance est désormais direct dans `SpellResistance.resist(...)`.

## Hazennstuff — original → HC-SPELLCOMPAT1

Modifiés :

- `fabric.mod.json`
- `net/hazen/hazennstuff/registry/HnSAttributes.class`

But : base neutre correcte pour les attributs de pourcentage utilisés par les multiplicateurs.

## Haute Capitale RPG — TEST3 RC1 → RC2

Modifiés :

- `fabric.mod.json`
- `net/hautecapitale/rpg/ability/AbilityResolver.class`

Ajouté :

- `net/hautecapitale/rpg/ability/HcAvatarStaffGate.class`

La RC1 contenait déjà les correctifs main/off-hand, ordre explicite et abilities techniques Arsenal.

## AzureLibArmor — original → TEST3 RC1

Modifiés :

- `fabric.mod.json`
- `mod/azure/azurelibarmor/common/cache/texture/AnimatableTexture$AnimationContents$Texture.class`
- `mod/azure/azurelibarmor/common/cache/texture/AnimatableTexture$AnimationContents.class`
- `mod/azure/azurelibarmor/common/cache/texture/AnimatableTexture.class`
- `mod/azure/azurelibarmor/common/internal/mixins/MixinHumanoidArmorLayer.class`
- `mod/azure/azurelibarmor/common/internal/mixins/TextureManagerMixin.class`
- `mod/azure/azurelibarmor/common/render/armor/AzArmorModel.class`
- `mod/azure/azurelibarmor/common/render/armor/AzArmorRendererPipeline$1.class`
- `mod/azure/azurelibarmor/common/render/armor/AzArmorRendererPipeline.class`

## Witcher RPG — référence HC → TEST3 RC1

Modifié :

- `fabric.mod.json`

Ajouté :

- `assets/witcher_rpg/textures/mob_effect/footwork.png`

Aucune suppression.

## Arsenal

Pas de reconstruction supplémentaire pendant la phase finale : le JAR `HC-FR-PRIMARY-ORDER1` est conservé.

Les corrections d’autorisation/déduplication sont dans Spell Engine + CapSkills.

## capitale_skills_items

Pas de reconstruction supplémentaire pendant la phase finale : `1.7.18-event-access-fix` reste la version de référence.

Deux anciennes références `capskills_0104:*` ont été observées dans du code legacy non enregistré/non atteignable ; elles ne sont pas considérées comme un chemin runtime actif.

## CapSkills

La version finale est RC2F.

Historique utile :

- RC2B : autorisation dynamique Arsenal ;
- RC2C : nettoyage de diagnostics/version, pas de changement gameplay majeur ;
- RC2D : tentative Avatar/tag qui a été abandonnée ;
- RC2E : première restauration de l’arbre ;
- RC2F : restauration dure de tout `data/capskills/puffish_skills/` depuis RC2B.

Ne pas repartir de RC2D.
