# Armor Balance 0.1.6 + Spell Engine RC17

## Correction après audit de couverture

L'audit précédent a trouvé un oubli réel : `myths_of_the_sea` charge le set Bake Kujira mais aucun de ses IDs n'était vu dans les 953 conversions de la passe 0.1.2.

La cause est architecturale : le rewrite `DefaultItemComponentEvents` dépend du moment où les composants sont enregistrés. Il peut donc manquer des armures dont les attributs sont construits autrement ou plus tard.

## Armor Balance 0.1.6

La 0.1.6 supprime cette dépendance au timing pour les armures standards.

Nouveau point d'injection Minecraft 1.21.11 :
- classe intermédiaire `net.minecraft.class_1741` = ArmorMaterial ;
- méthode `method_63993` = `ArmorMaterial.createAttributeModifiers(EquipmentType)` ;
- injection à RETURN.

Le `AttributeModifiersComponent` retourné est copié et seul `GENERIC_ARMOR + ADD_VALUE` sur les slots d'armure est doublé.

Cela couvre :
- vanilla ;
- les ArmorItem/ArmorMaterial standards des mods ;
- Bake Kujira / Myths of the Sea ;
- les autres mods standards indépendamment de leur namespace.

Les armes, toughness, max health, spell power et opérations multiplicatives restent inchangés.

Le mixin de formule ARMOR40 est byte-for-byte identique à 0.1.5.

Artifact :
`capitale_armor_balance-0.1.6+1.21.11-ARMOR40-ARMOR-MATERIAL-RPG.jar`

SHA-256 :
`f42d824122bb482c3e0494f9c2ffef05a28b99d1554fe40273b60c9babdfcfb2`

## Spell Engine RC17

RC17 reste inchangé :
- patch de `net.spell_engine.rpg_series.item.Armor.attributesFrom(...)` ;
- `ArmorSetConfig.Piece.armor` x2 ;
- couvre les familles RPG Series utilisant `Armor.register(...)` sans whitelist d'IDs.

Artifact :
`spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC17-RPG-ARMOR-X2.jar`

SHA-256 :
`64e31c2239b2e341c5ae514ca522814f36549c819fa88a7e7bf7a5186bc75fbd`

## Couverture attendue

- vanilla : ArmorMaterial -> 0.1.6 ;
- armures standards moddées : ArmorMaterial -> 0.1.6 ;
- RPG Series / Armory / classes RPG : Armor.attributesFrom -> RC17 ;
- Myths of the Sea Bake Kujira : ArmorMaterial -> 0.1.6.

Bake Kujira attendu :
`2/4/5/2 -> 4/8/10/4 = 26`.

Justicar attendu :
`3/8/6/3 -> 6/16/12/6 = 40`.

## Death Knights

Le runtime voit 16 IDs Death Knights dans l'ancienne passe générique. Le build exact `death_knights-1.0.0+1.21.11.jar` n'est pas archivé dans le dépôt accessible, donc son chemin final reste à valider en runtime avec 0.1.6 + RC17.

Ne pas promouvoir comme version serveur finale avant :
1. démarrage client + serveur ;
2. vanilla = x2 ;
3. Armory Justicar = 40 ;
4. Bake Kujira = 26 ;
5. un set Death Knights = x2 ;
6. armes/toughness/stats magiques inchangées.
