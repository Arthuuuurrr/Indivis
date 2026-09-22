# Matrice de tests runtime

Cette matrice doit être exécutée avec les mêmes versions côté client et serveur.

## Démarrage

- [ ] client démarre sans linkage/mixin/entrypoint error ;
- [ ] serveur démarre sans linkage/mixin/entrypoint error ;
- [ ] aucune ancienne RC du même mod n’est présente en doublon ;
- [ ] le log charge Arsenal côté client **et** serveur ;
- [ ] le log charge `capitale_skills_items` côté serveur ;
- [ ] Spell Power affiché = RC8, jamais RC6/RC7.

## CapSkills

- [ ] arbre visible ;
- [ ] branches principales visibles ;
- [ ] branche métiers visible conformément au design ;
- [ ] connexions Sorceleur correctes ;
- [ ] reconnexion sans disparition des skills ;
- [ ] changement de dimension sans perte d’état.

## Spellbar / containers

- [x] Mage de feu observé sans sort en double sur le test du 22/09/2026 ; la matrice multi-catégories reste ouverte.
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

## HUD / icônes — Spell Engine RC10 (hérité de RC9)

- [ ] spellbar à la même hauteur que la hotbar vanilla (`y=-11`) ;
- [ ] aucun chevauchement avec cœurs/armure ;
- [ ] décalage de 15 px vers la gauche suffisant (`x=-185`) ;
- [ ] ancien défaut HC `(-170,-34)` migre vers `(-185,-11)` ;
- [ ] ancien défaut upstream `(-170,-11)` migre vers `(-185,-11)` ;
- [ ] position custom différente des anciens défauts inchangée ;
- [ ] migration persistée dans `hud_config.json` ;
- [ ] technique clic droit de la hache (« Coupe profonde ») affiche l’icône CapSkills ;
- [ ] tester au moins une technique de masse, lance, épée et arme à deux mains ;
- [ ] un sort non mappé conserve son icône/fallback upstream.

## Spell Engine RC10 — canalisations / durées

- [ ] AOE CHANNEL sans cible (ex. Fire Storm / Whirlwind / chants de barde) : effets actifs et fin après une seule durée ;
- [ ] maintenir la touche après la fin d'un CHANNEL ne redémarre pas un nouveau cycle ;
- [ ] relâcher puis appuyer à nouveau démarre un nouveau CHANNEL normalement ;
- [ ] AIM required CHANNEL avec cible valide (Wind Updraft / Vicious Mockery) : cast fini ;
- [ ] AIM required CHANNEL sans cible : comportement d'échec attendu, jamais de boucle infinie ;
- [ ] AIM optional CHANNEL (Arcane Missile / Penance) : cast fini ;
- [ ] BEAM CHANNEL (Arcane Beam / Hydro Beam / Holy Beam) : cast fini ;
- [ ] relâchement anticipé avec holdToCastChannelled=true annule toujours correctement ;
- [ ] STANDARD inchangé ;
- [ ] CHARGE inchangé ;
- [ ] aucun cast anormalement long avec bonus/malus Hazenn de cast/cooldown ;
- [ ] client et serveur chargent tous deux Spell Engine RC10.

## Spell Power RC8 — régression ClassFormat

- [ ] serveur démarre avec RC8 ;
- [ ] dégâts sur un mob déclenchent SpellResistance sans ClassFormatError ;
- [ ] dégâts sur un joueur déclenchent SpellResistance sans ClassFormatError ;
- [ ] laisser tourner avec des mobs chargés sans crash de tick ;
- [ ] latest.log ne contient ni `ClassFormatError` ni `HcHazennSpellResistanceMixin` ;

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
