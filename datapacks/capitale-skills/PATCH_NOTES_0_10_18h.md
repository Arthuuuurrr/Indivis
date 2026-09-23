# CapSkills 0.10.18h — PRIMARY SPELL ORDER

Base : 0.10.18g.

## Objectif
Les attaques principales affichées directement par les bâtons doivent être apprises avant les sorts secondaires de leur école. Aucun nœud existant n'a été déplacé.

## Wizard
- Arcanes : `Arcane Blast` devient l'unique premier sort après Mage arcanique.
- Feu : `Fire Blast / Pyroblaste` devient l'unique premier sort après Mage de feu.
- Givre : `Frostbolt / Éclair de givre` devient l'unique premier sort après Mage de givre.

## Elemental Wizards
Trois sorts principaux de bâton avaient disparu de l'arbre et ont été restaurés :
- Eau : `aqua_water_whip` — Fouet d'eau.
- Terre : `terra_stone_spear` — Lance de pierre.
- Air : `wind_air_cutter` — Lames d'air.

Ils sont insérés dans l'espace vertical libre entre le nœud de classe et les sorts déjà placés. Aucun ancien x/y n'est modifié. Ils sont staff-only conformément au remapping précédent.

## Martial
Aucun changement : les techniques `rpg_series` liées aux armes ont toutes un vrai temps de recharge et ne constituent pas une attaque principale répétable analogue aux sorts de bâton.
