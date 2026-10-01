# Validation finale — Haute Capitale Dialogue b9 / Core RC9BA

## Entrées

- b8 original/backup SHA-256 :
  `428c771ef958e56245628bb145a198f04715e5e7a21d5a855ede673b5c7b25c0`
- patch b9 compilé par GitHub Actions :
  `b9a63adff0372eb1a9536f23575b9221ecbaab566335e2abd1d2100f194cd2fb`

## Sorties produites

- `haute-capitale-dialogue-0.1.0+1.21.11.b9.jar`
  SHA-256 `fedf37a5bc3a6bf6364bdd421ad1a2c1955679a6eeb07467252274838139812a`
- `capitale_core_1.5.6-RC9BA_DIALOGUE_INTERPELLATIONS_FULL.zip`
  SHA-256 `c8e1df244702100fb3f02568e2718b11dd132c9a7bbe92f6cbefb1f7e0152b73`

## Contrôles b9

- build/remap GitHub Actions : PASS ;
- validation du contenu du patch : PASS ;
- archive finale b9 : PASS ;
- `fabric.mod.json` : version b9, licence ARR retirée, entrypoints b8 conservés ;
- aucune entrée du b8 supprimée ;
- aucune entrée b8 hors `fabric.mod.json` modifiée ;
- `DialogueCameraController.class` inchangé ;
- `DialogueManager.class` inchangé ;
- `DialoguePanel.class` inchangé, y compris le comportement `...` ;
- `CaptureDialogueScreen.class` inchangé ;
- `RpgDialogueScreen.class` inchangé ;
- `MessageCapture.class` inchangé ; le filtrage est ajouté par mixin.

## Contrôles RC9BA

Diff structurel contre RC9AY :
- 5 fonctions de quête modifiées pour ajouter uniquement un appel
  `hcd_interpellation nearest_named ...` ;
- `pack.mcmeta` mis à jour ;
- fichier de validation ajouté ;
- aucune ressource RC9AY supprimée.

Hooks de quête :
- Géraud Rivet ;
- Althéon Brumeforge ;
- Léovic ;
- Aurèle Veyrane ;
- Roch Vallet.

Les contrôles d'accès sont gérés par le filtre b9 du flux `CapChoix` correspondant au refus avec
« suivre le garde » / « tenter un arrangement » ; les simples lignes de proximité ne déclenchent
pas l'interpellation caméra.

## Portée de la validation

Ceci valide la compilation, l'ABI ciblée et la préservation binaire du b8. Le comportement en jeu
reste à valider sur le serveur réel avec EasyNPC et les datapacks actifs.
