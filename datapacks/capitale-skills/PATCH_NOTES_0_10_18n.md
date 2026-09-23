# CapSkills BETA 0.10.18n — Witcher weapon tag compatibility fix

- Remplace les 53 dépendances runtime à `#witcher_rpg:witcher_swords` par le tag de compatibilité interne `#capitale:weapon/witcher_swords`.
- Le tag interne contient explicitement les 19 épées de Sorceleur enregistrées par le JAR Witcher 3.1.0 utilisé par Haute Capitale.
- Les entrées sont optionnelles (`required: false`) pour éviter qu’une variante retirée dans une future build invalide le datapack entier.
- Inclut notamment `yrden_sustained_glyphs` et toutes les autres capacités Sorceleur, pas uniquement les avertissements copiés depuis le log.
- Aucun skill, coût, coordonnée ou connexion de l’arbre n’est modifié.
