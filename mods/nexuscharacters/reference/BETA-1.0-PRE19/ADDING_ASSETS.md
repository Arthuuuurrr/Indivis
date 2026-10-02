# Ajouter des cosmétiques à la PRE19

Les textures ajoutées dans les dossiers ci-dessous sont détectées au démarrage
ou après F3+T. Elles entrent dans les boutons de sélection, les marqueurs
d'apparence et le rendu 3D sans changement de code.

Tous les chemins sont relatifs à la racine du JAR ou d'un pack de ressources.

| Catégorie | Dossier | Noms conseillés pour les prochains ajouts |
|---|---|---|
| Coiffures | `assets/nexuscharacters/appearance_parts_v064/hair/` | `hair_14.png`, `hair_15.png`, etc. |
| Barbes et moustaches classiques | `assets/nexuscharacters/appearance_parts_v068/facial_hair/` | `facial_07.png`, `facial_08.png`, etc. |
| Tenues | `assets/nexuscharacters/appearance_parts_v064/outfits/` | `outfit_39.png`, `outfit_40.png`, etc. |

- Conserver des PNG **64 × 64**, sur les UV du skin Minecraft, avec transparence
  hors du vêtement ou du cosmétique. Pour les éléments recolorables, utiliser
  les niveaux de gris comme les assets actuels.
- Utiliser un numéro unique et permanent. Ne pas renuméroter les fichiers lors
  d'un ajout : le numéro fait partie de l'apparence sauvegardée. Les trous dans
  les numéros sont supportés. Plage des nouveaux identifiants : 1 à 9999.
- Pour les cheveux, choisir une seule convention pour les nouveaux ajouts.
  Les anciens noms restent compatibles : `hair_long_09.png` correspond à
  l'identifiant 14 et `hair_short_06.png` à 1006. Ne pas créer aussi `hair_14.png`
  pour un style différent : ce serait le même identifiant.
- Pour les barbes classiques, préférer impérativement les noms numérotés
  `facial_NN.png` si leur identité doit rester stable après les ajouts suivants.
  Les noms libres sont détectés, mais leur index dépend du tri des fichiers.
- Un même numéro dans un pack de ressources remplace le même asset ; il ne
  crée pas une deuxième option. Tous les clients doivent recevoir les textures
  utilisées. Le serveur ne distribue pas automatiquement les nouveaux PNG.
- Les pixels de base et de surcouche gardent leurs UV et leurs couleurs.
  Deux profondeurs de voxels sont construites séparément, sans fusion des textures.
  Le volume est construit à partir des pixels des cosmétiques et tenues : pas
  de modèle 3D manuel à ajouter pour ces textures. Les barbes déjà modélisées,
  notamment celles des nains, gardent leur système actuel ; ajouter un modèle
  culturel inédit reste un autre type de modification.

L'ajout de ces trois types de textures est couvert. Un nouveau type de cosmétique
avec une autre structure de données ou un autre système de modèles n'est pas
automatiquement pris en charge.
