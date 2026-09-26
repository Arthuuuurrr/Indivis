# BROKEN — Capitale Creatures 1.2.35

**Ne pas utiliser. Version supersédée par 1.2.37.**

La première implémentation du clic droit a injecté un descripteur JVM invalide pour `Player.method_7270(ItemStack)` :
`(Lnet/minecraft/class_1799)Z` au lieu de `(Lnet/minecraft/class_1799;)Z`.

Elle appliquait également à tort la logique `VARIANT` à `KomodoDragonEgg`, alors que cette classe ne possède pas cette propriété.

Conservé uniquement pour l'historique de l'issue #116.
