# RPG-HUD 3.13 — Haute Capitale HC40M 1.0 — INVALIDE

Cette version **ne doit plus être utilisée**.

Le JAR HC40M 1.0 contenait une classe Mixin injectée manuellement dont le bytecode ne portait pas réellement l'annotation `@Mixin`. Le client échouait pendant la préparation Mixin avec :

`InvalidMixinException: HauteCapitaleClockMixin is missing an @Mixin annotation`

Le JAR et son checksum ont été supprimés de `mods/`. Les anciennes sources de patch ont également été retirées de la branche principale ; leur historique reste disponible dans Git.

Version de remplacement :

`mods/RPG-HUD-3.13-HauteCapitale-HC40M-1.0.1.jar`

La 1.0.1 utilise une classe réellement compilée avec Fabric Loom et a passé un lancement client Fabric 1.21.11 avec RPG-HUD 3.13 sans erreur Mixin.

Voir : `docs/mods/RPG_HUD_3_13_HC40M_1_0_1_VALIDATION.md`.
