# Haute Capitale — Armor Balance 0.1.1

Issue suivie : #49.

Premier passage d'équilibrage pour l'échelle ARMOR40.

## Principe

Le module double uniquement les valeurs additives de `minecraft:generic.armor`
portées par des items **non vanilla** et affectées à un vrai slot d'armure
(HEAD/CHEST/LEGS/FEET/ARMOR/BODY).

Sont laissés inchangés :

- armures vanilla ;
- armor toughness ;
- max health ;
- knockback resistance ;
- attributs Spell Power / magie ;
- bonus ARMOR d'accessoires ou de main ;
- modifiers ARMOR multiplicatifs.

Cela évite de maintenir une copie modifiée de chaque JAR d'armures et couvre
également Armory RPGs, MagistuArmory, Nightreign Armor, Hazennstuff et les mods
de classes tant qu'ils utilisent les composants d'attribut standard de 1.21.11.

## Formule ARMOR40

L'ancien patch HUD autorisait/affichait 40 points mais la réduction des dégâts
restait plafonnée par la formule vanilla à 20. Le mixin de ce module transpose
également `DamageUtil.method_5496` sur une échelle 0..40 :

- input intermédiaire ×2 ;
- plafond 20 → 40 ;
- diviseur 25 → 50 ;
- résultat final ×0,5.

Pour une ancienne armure modée de valeur A devenue 2A, la protection est
mathématiquement identique à l'ancienne courbe, toughness comprise. Les écarts
relatifs entre sets sont donc préservés pour ce premier passage.

Les armures vanilla ne sont volontairement pas doublées et se retrouvent ainsi
plus basses dans la nouvelle échelle de progression.

## Diagnostic runtime

Au démarrage, chaque item réellement modifié est loggé :

`[capitale_armor_balance] x2 ARMOR namespace:item [ancien→nouveau]`

Si une armure modée attendue n'apparaît jamais dans ces logs, elle fournit son
armure par un chemin non standard et pourra être traitée séparément.

## Artifact

`capitale_armor_balance-0.1.1+1.21.11-ARMOR40-X2-FORMULA.jar`

SHA-256:
`53b63960901f9f17f64cf6fb8a1eb9a1a128c2762fddaf90e7bccfd8c34e2571`

Installation : client + serveur, en complément des mods d'armure existants.
