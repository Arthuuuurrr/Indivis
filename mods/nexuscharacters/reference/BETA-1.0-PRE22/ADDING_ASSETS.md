# Ajouter des assets compatibles PRE22

Utiliser les mêmes noms, dossiers et UV que les fichiers existants dans `appearance_parts_v064/hair`, `appearance_parts_v064/outfits` et `appearance_parts_v068/facial_hair`. Le catalogue dynamique découvre les ajouts, même si leurs indices ne sont pas contigus. Les variantes HD sont normalisées vers les UV 64×64 du compositeur.

Les pixels de la couche supérieure ont priorité. Chaque pixel cosmétique inférieur opaque sans pixel supérieur correspondant reçoit automatiquement une empreinte extérieure. Les masques des tenues, cheveux et barbes classiques restent séparés lors de la génération native des parois 3D. Une zone transparente dans les deux couches reste vide.

Les textures sont chargées par le gestionnaire de ressources, donc les remplacements de resource packs restent possibles. Les accessoires ou barbes déjà modélisés gardent leur chemin de rendu.

Le banc charge une nouvelle coiffure, une coiffure HD, une tenue, une barbe et une moustache depuis son propre pack de test et vérifie leur présence au catalogue ainsi que leur rendu, sans code spécifique à ces indices dans le mod.
