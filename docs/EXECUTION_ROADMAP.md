# Roadmap d'exécution — Indivis

Cette roadmap organise les **issues ouvertes** selon leurs dépendances réelles. Elle ne remplace pas les issues : elle indique **dans quel ordre les traiter**.

## Règle de pilotage

- **P1** : axe principal actuel.
- **P2** : à traiter juste après les P1 ou en parallèle si indépendant.
- **P3** : secondaire.
- **P4** : backlog.
- **VERIFY / A tester / Valider** : priorité aux tests avant de repartir en développement. Un correctif déjà produit doit être soit **validé et fermé**, soit **rebasculé en bug actif**.
- Limite recommandée : **2 chantiers de développement actifs + 1 chantier de validation** à la fois.

---

# Phase 0 — Fermer les incertitudes techniques

Objectif : savoir précisément ce qui fonctionne déjà avant de modifier davantage le modpack.

## 0A — Validations P1

1. **#55 — Arbre CapSkills vide après reconstruction**
   - vérifier RC2F en jeu ;
   - confirmer toutes les branches et dépendances ;
   - si OK : fermer ;
   - si KO : devient blocant pour #53.

2. **#58 — NexusCharacters 0.7.7**
   - autorité serveur ;
   - isolation personnages ;
   - sauvegarde crash/déconnexion ;
   - aucun import d'état provenant du solo ;
   - test multijoueur.

3. **#7 — Dédoublement des spells**
   - matrice multi-catégories ;
   - instantané, canalisé, projectile, AoE, ciblé, arme, école ;
   - clic droit répété/maintenu ;
   - reconnexion.

## 0B — Validations rapides P2/P3

4. **#56 — HUD : disparition du spam `PAYLOAD accepted`**
5. **#57 — Adventure : feu et soul fire**
6. **#60 — MMO Music Zones**
7. **#61 — capitale_creatures_bundle sans datapack**
8. **#62 — Prologue complet**
9. **#66 — Bas-Anneaux Q04**

### Gate de sortie Phase 0

La phase est terminée quand :
- les correctifs réellement fonctionnels sont fermés ;
- chaque échec de test est transformé en bug actif clairement reproductible ;
- aucun système central n'est encore dans un état « censé être corrigé mais on ne sait pas ».

---

# Phase 1 — Stabiliser le socle progression / combat / équipement

Objectif : faire de CapSkills + système de spells la source d'autorité unique avant d'ajouter davantage de contenu combat.

## Ordre conseillé

### 1A — Autorité et progression

1. **#53 — Unifier spells, skills, armes et armures**
2. **#3 — Reset des compétences de tir**
3. **#4 — Compétences armes à feu invisibles / barre absente**
4. **#5 — Arsenal ↔ sorts/compétences**
5. **#6 — Haznstuff ↔ sorts/compétences**
6. **#18 — Terra Staff : tag / skill / spell**

### 1B — Équipement et attributs

7. **#2 — Crash heal + armure enchantée**
8. **#15 — Max Health > 10 cœurs**
9. **#16 — MMO Accessories : déséquipement / lifesteal**
10. **#49 — Buff armure de mage**

### 1C — Combat et ciblage

11. **#14 — Friendly fire / ciblage groupe**
12. **#50 — Heal bloqué par mobs/bosses**
13. **#19 — Cooldown après roulade**
14. **#48 — Pyroblaste vs Boule de feu**

### Gate de sortie Phase 1

- reset réellement autoritaire ;
- aucun accès à une arme/sort non débloqué ;
- HUD cohérent ;
- reconnexion et changement de dimension sans perte ni contournement ;
- armures/accessoires sans duplication d'attributs ;
- aucun crash de heal ;
- matrice de compatibilité #53 remplie.

---

# Phase 2 — Biomes, cultures, villes safe et spawns

Objectif : définir l'infrastructure monde avant de multiplier les villes, musiques et populations.

## Ordre obligatoire par dépendance

### 2A — Référentiel culturel

1. **#43 — Définir les biomes de référence par culture**
   - humains, elfes, nains, etc. ;
   - variantes compatibles ;
   - règles de musique et de spawn.

### 2B — Biomes techniques

2. **#35 — Biomes techniques, villes et musiques**
3. **#52 — Biomes de ville safe**
   - aucun spawn naturel indésirable ;
   - spawns scriptés conservés ;
   - musique propre à la ville/quartier.

### 2C — Populations et variations

4. **#38 — Variations de spawns selon biome/culture/zone**
5. **#47 — Spawnrate dinos désert/mesa**
6. **#10 — Monstres marins / Léviathan**
7. **#39 — Points d'intérêt avec mobs dangereux / quêtes**
8. **#24 — Tigrée : spawn et difficulté**

### Gate de sortie Phase 2

Pour chaque culture majeure :
- biome de référence défini ;
- biome urbain safe défini ;
- musique correcte ;
- règles de spawn documentées ;
- aucune apparition sauvage incohérente en ville ;
- cas particuliers testés.

---

# Phase 3 — Quêtes, dialogues et progression narrative

Objectif : fiabiliser les chaînes existantes avant d'en produire beaucoup de nouvelles.

## 3A — Validation prologue

1. **#62 — Prologue complet**
2. Corriger les éventuelles régressions trouvées avant de continuer.

## 3B — Bas-Anneaux

3. **#63 — Q01 Mira**
4. **#64 — Q02 Elias**
5. **#65 — Q03 Lysandre**
6. **#66 — Q04**

## 3C — Bugs de progression existants

7. **#8 — Tobie Lescure partie 2**
8. **#9 — Magistrat / rang visiteur**
9. **#20 — Dialogues trop rapides**
10. **#21 — Consommable utilisé en boucle en dialogue**
11. **#25 — Curseur de quête**
12. **#26 — Objets de quête persistants**
13. **#59 — FPS dialogues / caméra NPC**

## 3D — Production

14. **#33 — Quêtes de la capitale pour la bêta fermée**

### Gate de sortie Phase 3

- prologue rejouable de bout en bout ;
- Q01–Q04 rejouables de bout en bout ;
- pas de progression silencieusement bloquée ;
- dialogues stables et lisibles ;
- reconnexion supportée ;
- nouveaux contenus construits sur une structure de quête validée.

---

# Phase 4 — Donjons, boss et loot

Objectif : stabiliser un modèle de donjon réutilisable avant d'en produire plusieurs.

## Ordre conseillé

1. **#32 — Premiers donjons instanciés**
2. **#51 — Rework Tuff Brick**
3. **#11 — Donjon Orc : Warlock / hitboxes / éléments récupérables**
4. **#12 — Boss qui drop des blocs**
5. **#13 — Boss trop puissant**
6. **#46 — Sorts de boss invisibles**
7. **#28 — Loot de coffre par joueur**
8. **#23 — Jarres : interaction**
9. **#22 — Flèches de mobs au sol**

### Gate de sortie Phase 4

Un donjon de référence doit avoir :
- instance/reset propres ;
- spawns bornés ;
- boss lisible ;
- difficulté cohérente ;
- aucun bloc/objet de structure exploitable ;
- loot par joueur ;
- reconnexion/sortie propre.

---

# Phase 5 — Construction bêta et contenu secondaire

Objectif : remplir le monde une fois les systèmes structurants suffisamment stables.

## P2/P3

- **#34 — Terraforming continent capitale**
- **#37 — Temple des Six**
- **#40 — Renommer/traduire les sets d'armures**
- **#45 — Nettoyer les error/warn console**
- **#27 — Entité « Humanoid »**

## P4 / backlog

- **#29 — Housing**
- **#30 — Montures**
- **#31 — Nodes de minerais**
- **#36 — Langues elfique/naine + polices**
- **#41 — Intérieur du palais**
- **#42 — Cinématique montgolfière**
- **#44 — Noms des villes humaines**

Ces tâches peuvent avancer en parallèle si elles ne modifient pas un système encore instable.

---

# Ordre de travail recommandé immédiatement

Pour éviter de disperser Arthur et Clément :

## Validation
1. #55 CapSkills
2. #58 NexusCharacters
3. #7 dédoublement spells

## Développement principal
1. #53 unification spells/skills/armes/armures
2. #43 biomes de référence par culture

## Développement secondaire
- corriger uniquement les échecs révélés par les tests Phase 0 ;
- éviter d'ouvrir simultanément plusieurs nouveaux chantiers P2/P3.

---

# Règle de fermeture d'une issue

Une issue n'est fermée que si :
- le correctif est dans la branche stable ;
- le scénario de reproduction ne fonctionne plus ;
- aucune régression évidente n'apparaît ;
- le test est fait sur l'environnement pertinent (serveur/client si nécessaire) ;
- les logs ne montrent pas une nouvelle erreur associée.

Pour les issues marquées **A tester / Valider**, un audit statique seul ne suffit pas.
