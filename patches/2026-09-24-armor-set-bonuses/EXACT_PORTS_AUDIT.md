# Audit des ports exacts retrouvés

## Death Knights — HC-FR-NATIF1

Binaire audité :
`death_knights-1.0.0+1.21.11-HC-FR-NATIF1.jar`

Le bytecode de `elocindev.deathknights.registry.ArmorRegistry` référence directement :
- `net.spell_engine.rpg_series.item.Armor.Entry`;
- `ArmorSetConfig.Piece`;
- `AttributeModifier.multiply/bonus`;
- `Armor.register`;
- les écoles Spell Power Frost, Blood et Unholy.

Conclusion : ce port est bien RPG-Series-native. Il ne doit pas être traité comme un mod d'armure tiers à intégrer artificiellement.

## Lands of Icaria — HC-FR-NATIF1

Binaire audité :
`landsoficaria-1.0.0-HC-FR-NATIF1.jar`

`IcariaArmorItem` construit ses composants via l'ArmorMaterial/EquipmentType vanilla et ne contient pas de tick, callback ou attribut secondaire de combat. Le seul comportement propre à l'item est le drapeau de marche sur neige poudreuse.

Conclusion : aucune adaptation Spell Engine/CapSkills à faire sur ses armures.

## Haute Capitale Métiers — b12

Binaire audité :
`haute-capitale-metiers-0.1.0.b12.jar`

185 classes inspectées. Aucun item/classe d'armure, helmet/chestplate/leggings/boots, ni dépendance Spell Engine/Spell Power liée à l'équipement défensif n'a été trouvée.

Conclusion : ce mod n'appartient pas au chantier d'équilibrage des armures. Ses gadgets/repas sont un sujet séparé.
