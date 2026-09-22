# Matrice de tests runtime

Cette matrice doit être exécutée avec les mêmes versions côté client et serveur.

## Démarrage

- [ ] client démarre sans linkage/mixin/entrypoint error ;
- [ ] serveur démarre sans linkage/mixin/entrypoint error ;
- [ ] aucune ancienne RC du même mod n’est présente en doublon ;
- [ ] le log charge Arsenal côté client **et** serveur ;
- [ ] le log charge `capitale_skills_items` côté serveur ;
- [ ] Spell Power affiché = RC7, jamais RC6.

## CapSkills

- [ ] arbre visible ;
- [ ] branches principales visibles ;
- [ ] branche métiers visible conformément au design ;
- [ ] connexions Sorceleur correctes ;
- [ ] reconnexion sans disparition des skills ;
- [ ] changement de dimension sans perte d’état.

## Spellbar / containers

- [ ] Fireball : une entrée, un cast ;
- [ ] pas de doublon après changement d’arme ;
- [ ] main hand → ability mise à jour immédiatement ;
- [ ] off hand → ability mise à jour immédiatement ;
- [ ] reconnexion conserve un container cohérent.

## Arsenal

- [ ] `/give arsenal:...` ne déconnecte plus le joueur ;
- [ ] arme normale : passif bloqué avant skill ;
- [ ] arme normale : passif autorisé après skill ;
- [ ] superweapon : choix actif accepté ;
- [ ] passif superweapon seulement si le choix actif est appris/compatible ;
- [ ] dual wield même passif : un seul trigger par événement ;
- [ ] shield offhand : gate correct.

## Bâton de l’Avatar

Pour `elemental_wizards_rpg:unique_staff_1` :

- [ ] choix Terra → Stone Spear disponible si appris ;
- [ ] Terra ne donne pas Water Whip ;
- [ ] Terra ne donne pas Air Cutter ;
- [ ] choix Water → Water Whip uniquement ;
- [ ] choix Air → Air Cutter uniquement ;
- [ ] possession seule ne donne pas Frost Elemental ;
- [ ] possession seule ne donne pas Fire Hydra ;
- [ ] possession seule ne donne pas Spirit Wolf ;
- [ ] possession seule ne donne pas Bearward ;
- [ ] possession seule ne donne pas Earth Golem.

## Heal / ciblage

- [ ] Heal : allié derrière un mob ciblable ;
- [ ] Flash Heal : allié derrière un boss ciblable ;
- [ ] mur solide bloque toujours la cible ;
- [ ] Holy Shock inchangé ;
- [ ] Holy Beam inchangé ;
- [ ] caster mob/NPC peut toujours cibler une entité non-player conformément à upstream.

## AzureLibArmor

- [ ] armure Azure enchantée + outline : aucun crash ;
- [ ] pendant outline : pas de chemin glint problématique ;
- [ ] hors outline : glint normal restauré.

## HUD

- [ ] config historique `BOTTOM, y=-11` migre à -34 ;
- [ ] valeur custom autre que -11 inchangée ;
- [ ] migration persistée dans `hud_config.json`.

## Hazenn

- [ ] bonus Fire augmente seulement les écoles attendues ;
- [ ] Lightning augmente Air ;
- [ ] Nature n’augmente pas Earth ;
- [ ] Ender/Cosmic/Eldritch n’augmentent pas Arcane par défaut ;
- [ ] set Mage : casting movement + spell resist fonctionnels ;
- [ ] Fireblossom Ruler : casting movement + spell resist fonctionnels ;
- [ ] Tyrant's Grace : casting movement + Fire power fonctionnels ;
- [ ] Summoner : +15 % dégâts d’invocation sans buff des sorts normaux du joueur.

## Witcher

- [ ] icône Footwork visible ;
- [ ] Fondamentaux → Escrime → Maîtrise mêlée ;
- [ ] Signes fondamentaux → Sign de base → Maîtrise Signes.

## Critère de fermeture

Une issue ne doit être fermée qu’après :

- reproduction initiale connue ;
- correctif installé sur l’environnement pertinent ;
- scénario passé ;
- absence de nouvelle erreur correspondante dans le log.
