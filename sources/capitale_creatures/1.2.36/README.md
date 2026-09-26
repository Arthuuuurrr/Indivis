# BROKEN — Capitale Creatures 1.2.36

**Ne pas utiliser. Version supersédée par 1.2.37.**

1.2.36 a remplacé l'appel actif par le bon descripteur `(Lnet/minecraft/class_1799;)Z`, mais la reconstruction avait réutilisé le constant pool de 1.2.35. L'ancien `NameAndType` invalide `(Lnet/minecraft/class_1799)Z` est donc resté dans la classe, et la JVM le rejetait toujours avec `ClassFormatError` au chargement.

Cette version héritait également de la conception erronée qui supposait une propriété `VARIANT` sur `KomodoDragonEgg`.

La correction 1.2.37 repart directement de 1.2.34 et reconstruit entièrement le constant pool des deux classes modifiées.

Conservé uniquement pour l'historique de l'issue #116.
