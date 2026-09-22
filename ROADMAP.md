# Roadmap Indivis

Source initiale : suivi Discord visible entre le 2 juin et le 21 septembre 2026.

La roadmap stratégique reste ici. Le travail actif détaillé vit dans les **GitHub Issues**.

## Focus actuel — priorité P1

### A. Biomes, cultures, villes safe et spawns

Ordre de travail recommandé :

1. **Définir les biomes de référence par culture** (#43).
   - Exemple de base : humains → `plains`.
   - Exemple de base : elfes → `sunflower_plains`.
   - Compléter ensuite pour les autres cultures.
2. **Créer les biomes techniques de villes** (#35, #52).
   - zones urbaines sans spawns naturels indésirables ;
   - spawns scriptés/PNJ conservés ;
   - musique dédiée par ville/quartier ;
   - transition vers le biome culturel extérieur.
3. **Décliner les variations de spawns** (#38).
   - profil différent selon biome/culture ;
   - variantes régionales ;
   - densité et dangerosité ;
   - exceptions quêtes/POI/donjons.
4. **Stabiliser les cas particuliers** : dinos désert/mesa (#47), mobs marins (#10), points d'intérêt (#39).

### B. Système unifié spells / skills / armes / armures

L'objectif est que **CapSkills / le système de spells soit la source d'autorité** pour les déblocages et restrictions (#53).

Sous-chantiers prioritaires :

- Arsenal ↔ skills/spells (#5) ;
- Haznstuff ↔ skills/spells (#6) ;
- reset des compétences de tir (#3) ;
- affichage des compétences armes à feu (#4) ;
- tags/contraintes d'armes dont Terra Staff (#18) ;
- interactions armures/heal et crash associé (#2) ;
- accessoires/attributs persistants ou anormaux (#16) ;
- validation du correctif anti-dédoublement sur toutes les catégories de spells (#7).

Le dédoublement des spells n'est plus considéré comme « à corriger » : il **semble réglé**, mais doit passer une matrice de tests avant fermeture (#7).

## 1. Bêta fermée — stabilité et progression

### En cours

- ⛏️ Scénariser la quête principale.
- ⛏️ Scénariser et construire plusieurs lignes de quêtes.
- ⛏️ Ajouter suffisamment de quêtes dans la capitale pour la bêta fermée.
- ⛏️ Finaliser le terraforming du continent capitale.
- ⛏️ Construire le temple des Six.
- ⛏️ Renommer/traduire les sets et mods d'armures.
- ⛏️ Finir de définir les spawns de mobs.
- ⛏️ Ajouter les premiers donjons instanciés.
- ⛏️ Ajouter des points d'intérêt avec mobs dangereux / liés aux quêtes.
- ⛏️ Ajouter les biomes de ville, bordure de donjon et autres biomes techniques.
- ⛏️ Continuer l'intégration des musiques de biome.
- ⛏️ Continuer les langues elfique et naine et préparer les polices en jeu.

### Non traité

- 🔴 Construire l'intérieur du palais.
- 🔴 Ajouter une cinématique d'envol de montgolfière.
- 🔴 Créer une zone de farm de matériaux avec nodes de minerais.
- 🔴 Trouver des noms pour toutes les villes humaines.
- 🔴 Ajouter un système de loot de coffre par joueur.
- 🔴 Travailler le système de maisons / housing.
- 🔴 Travailler le système de montures.
- 🔴 Créer des personnages historiques célèbres de différentes races.
- 🔴 Régler les erreurs et warnings console des mods de Clément.
- 🔴 Adoucir la démarcation entre eaux profondes et peu profondes avec des sea pickles / détails.

## 2. Combat, compétences et classes

### Résolu historiquement

- ✅ Interface de création de personnage.
- ✅ Interface de sélection de personnage.
- ✅ Intégration initiale des mods spell/skill dans le système Pufferfish/CapSkills.
- ✅ Sauvegarde des sorts après déconnexion/reconnexion signalée comme réglée.
- ✅ Améliorations du healer signalées comme réglées.
- ✅ Choc sacré / heal clic droit : canalisation retirée et heal augmenté, signalé comme réglé.

### En cours ou à vérifier

- ⛏️ Relier Arsenal aux sorts/skills.
- ⛏️ Relier Haznstuff aux sorts/skills.
- 🧪 Valider le correctif du dédoublement des spells sur toutes les catégories.
- ⚪ Réduire le friendly fire de certains sorts, dont Earth Golem.
- ⚪ Revoir l'aggro de certains mobs de mêlée.
- ⚪ Différencier davantage Pyroblaste et Boule de feu.
- ⚪ Augmenter la durée de la musique de combat.
- ⚪ Buff armure de mage.
- ⚪ Permettre le heal à travers mobs/bosses.
- ⚪ Empêcher de cibler les membres du groupe avec des sorts offensifs.

## 3. Monde et donjons

- ⛏️ Définir les biomes de référence par culture.
- ⛏️ Créer des biomes de ville safe sans spawns naturels et avec musique dédiée.
- ⛏️ Créer des variations de spawns par biome/culture/zone.
- ⚪ Patcher certains boss, notamment les sorts invisibles.
- ⚪ Régler le spawnrate des dinos dans désert + mesa.
- ⚪ Revoir le donjon Tuff Brick : spawn infini, boucles, impasses, lisibilité des pièces.
- ⚪ Prévoir petites trappes/passages compatibles avec les différents gabarits de races.
- ⚪ Revoir le Scuttle qui one-shot dans le donjon.
- ⚪ Corriger les monstres marins, notamment le Léviathan, qui ne nagent pas.

## 4. Quêtes et UX

- ⚪ Corriger le magistrat du port qui ne donne plus le rang visiteur alors que le joueur reste étranger.
- ⚪ Corriger certaines répliques joueur de quêtes.
- ⚪ Revoir le curseur de quête qui « fuit » le regard ; piste : garder le waypoint et afficher le nom de l'objectif.

## 5. Éléments résolus / historiques

Voir [docs/RESOLVED.md](docs/RESOLVED.md).
