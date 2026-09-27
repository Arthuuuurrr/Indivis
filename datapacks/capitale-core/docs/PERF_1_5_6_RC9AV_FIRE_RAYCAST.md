# capitale_core 1.5.6-RC9AV — fire raycast PERF1

Issue : #130

## Cible

Le détecteur d'extinction du feu à main nue tourne à 20 Hz pour conserver un clic gauche réactif en mode Aventure.

Le raycast inspecte jusqu'à 20 positions (5 blocs par pas de 0,25). En RC9AU, chaque pas effectuait séparément :

- 2 tests pour `minecraft:fire` ;
- 2 tests pour `minecraft:soul_fire` ;
- puis le test d'arrêt sur bloc non remplaçable.

Dans le pire cas, cela représente jusqu'à 80 tests `if block` feu/soul_fire par joueur à main nue et par tick, avant même le reste du raycast.

## RC9AV

Ajout du tag :

`#capitale:adventure_bare_hand_fire`

Il contient uniquement :
- `minecraft:fire`
- `minecraft:soul_fire`

Les fonctions :
- `bare_hand_scan_step`
- `bare_hand_hit_step`

utilisent désormais ce tag commun.

Chaque pas passe donc de 4 tests feu/soul_fire à 2, sans modifier :

- cadence : toujours 20 Hz ;
- portée : toujours 5 blocs ;
- précision : toujours 0,25 bloc ;
- détecteur `minecraft:interaction` ;
- capture du clic gauche ;
- support `fire` et `soul_fire` ;
- arrêt sur bloc solide ;
- logique des outils/items avec `minecraft:can_break`.

## Risque fonctionnel

Faible : aucune condition métier n'est déplacée ou ralentie. Les deux IDs exacts testés auparavant sont seulement regroupés dans un block tag.

## Validation runtime

- [ ] main nue en Aventure : éteindre `minecraft:fire` ;
- [ ] main nue en Aventure : éteindre `minecraft:soul_fire` ;
- [ ] feu derrière un mur : impossible à éteindre ;
- [ ] feu à différentes distances jusqu'à la portée prévue ;
- [ ] outil/item avec `can_break` : comportement inchangé ;
- [ ] aucun détecteur `cap_fire_bare_detector` résiduel ;
- [ ] Spark comparable : vérifier une baisse du coût `CommandFunctionManager.tick`.

## État

Candidate PERF1. Aucun autre système du tick core n'est modifié dans cette passe.
