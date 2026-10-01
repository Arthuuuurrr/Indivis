# RPG-HUD 3.13 — Haute Capitale HC40M 1.0.1

Correctif de la version HC40M 1.0 invalide.

- JAR : `mods/RPG-HUD-3.13-HauteCapitale-HC40M-1.0.1.jar`
- Taille : **252354 octets**
- SHA-256 : `900ec16305aadde514279c0e8ca4b80666d4a1cef9ff7fe147b2e577e201f517`
- Base RPG-HUD 3.13 vérifiée : `ab2dec0daf697af037c294c50ba7a8dc5251048294d5f88229bc14d832a02b00`

## Validation

- classe Mixin compilée réellement avec Fabric Loom ;
- `javap -v` confirme `@Mixin` et `@Redirect` dans le bytecode ;
- lancement client Fabric 1.21.11 sous Xvfb avec le RPG-HUD officiel + le correctif ;
- aucune `InvalidMixinException`, `InjectionError` ou `MixinApplyError` ;
- JAR final conserve tous les fichiers upstream, sauf `fabric.mod.json` ;
- `rpg-hud.mixins.json` upstream reste byte-for-byte intact ;
- le correctif utilise son propre `hc_rpghud_hc40m_fix.mixins.json`.

Source : `sources/rpghud-hc40m-fix/`.
