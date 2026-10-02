# NexusCharacters PRE20 — ajout de cosmétiques

Le moteur découvre les fichiers de ressources au démarrage et après F3+T. Les ajouts peuvent être inclus dans le JAR ou fournis dans un pack de ressources client. Un fichier placé à côté du JAR dans le dossier `mods` n'est pas un asset chargé.

## Dossiers et identifiants

- Cheveux : `assets/nexuscharacters/appearance_parts_v064/hair/` ; `hair_long_09.png`, `hair_short_06.png`, ou `hair_15.png` pour un nouvel identifiant explicite. Les identifiants historiques 1–13 restent inchangés. Les ajouts `hair_short_06.png` et suivants utilisent des identifiants à partir de 1006 pour ne pas déplacer les coupes longues enregistrées.
- Tenues : `assets/nexuscharacters/appearance_parts_v064/outfits/outfit_39.png` et suivants. Les identifiants peuvent être espacés.
- Barbes/moustaches classiques : `assets/nexuscharacters/appearance_parts_v068/facial_hair/facial_07.png`, `facial_09.png`, etc. Elles suivent les règles de race existantes (humains et nordiques). Les barbes naines modélisées restent gérées par leur système 3D existant.

## Textures et relief

Utiliser le patron de skin Minecraft 64×64, avec transparence dans les espaces libres. La partie inférieure du patron reste plate ; la seconde couche fournit le relief natif Skin Layers (chapeau, veste, manches et pantalons). Les détails et trous de la seconde couche restent distincts : ils ne sont pas remplis avec la texture inférieure.

Pour un cosmétique dont une partie ne possède aucun pixel sur la seconde couche, PRE20 crée automatiquement son empreinte extérieure à partir des pixels inférieurs de cette partie. La couche inférieure est conservée pour le rendu de base et le rendu distant. Ce mécanisme concerne les nouvelles coiffures, les barbes classiques et les nouvelles tenues ; il ne dépend pas d'une liste de styles particuliers. Pour contrôler des détails plus fins, dessiner explicitement la seconde couche.

Les textures HD de taille carrée multiple de 64 sont ramenées à la grille 64×64. Préférer 64×64 pour vérifier précisément les raccords. Préparer les cheveux et les barbes en niveaux de gris pour leur colorisation commune. Les vêtements conservent leurs couleurs.

Les pixels de vêtements, cheveux et barbe qui occupent la même position extérieure sont composités dans l'ordre existant (tenue, cheveux, barbe), puis génèrent une seule surface. Cela évite de dessiner deux faces concurrentes. La géométrie native garde les bords et raccords des pixels, avec des limites aux articulations.

## Installation et essai

Remplacer l'ancien NexusCharacters par le JAR PRE20 ; conserver un seul JAR NexusCharacters dans `mods`. Le relief nécessite Skin Layers 3D côté client, avec ses options de couches activées. Le serveur conserve les identifiants et synchronise l'apparence ; le rendu du volume s'effectue côté client.

Après ajout/rechargement, vérifier le nouveau choix dans la création de personnage, le rendu de face/profil/dos, puis l'apparence en monde. Les fichiers de test ajoutés au banc de validation ne sont pas inclus dans le JAR livré.
