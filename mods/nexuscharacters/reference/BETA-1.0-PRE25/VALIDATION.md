# PRE25 — volume et séparation des cheveux

Correction ciblée des cheveux après le retour sur la PRE24. La construction des vêtements, les barbes, les proportions et les textures restent celles de la PRE24.

## Correction

Les cheveux sont construits en colonnes fermées sur la grille UV d'origine. Leur profondeur minimale est de 0,65 pixel de modèle, et de 1,15 pixel sur la face arrière du torse. Les détails supérieurs ajoutent 0,20 pixel. La profondeur est aussi bornée par l'enveloppe native de Skin Layers plus 0,20 pixel pour conserver la séparation lorsque les réglages de volume changent. Les flancs vont jusqu'au corps ; les volumes de découpage correspondent exactement aux faces produites.

Le profil s'applique à toutes les coiffures chargées par le catalogue, y compris les nouvelles textures. Les identifiants de test externes restent hors du JAR livré. La découverte suit toujours AJOUT_ASSETS.md.

La découpe locale des surfaces d’une même partie utilise une frontière mathématique commune, sans bande epsilon opposée entre les deux fragments. Cette bande laissait apparaître une ligne claire entre deux rangées de cheveux dans une vue strictement arrière. Un contrôle indépendant de surface détecte l’ancien défaut (1,999952 pixels carrés au lieu de 2) et passe sur la correction (2 exactement). Un contrôle de 41 échantillons de la capture arrière vérifie aussi que le vêtement ne traverse plus cette jonction.

## Correspondance avec le signalement

Les numéros affichés des tenues sont leur position dans le catalogue, pas leur nom de fichier. Les captures fournies montrent la coiffure 7 avec les tenues affichées 17 et 19 : fichiers outfit_22.png et outfit_24.png. Le banc utilise ces deux fichiers, la coiffure h07 et une couleur châtain.

## Vérifications du JAR livré

- Contrôle négatif : la PRE24 est rejetée six fois dans les deux aperçus du menu. Sa surface arrière est à 0,30 pixel, au niveau de l'enveloppe du vêtement. Les captures comparatives préservent ce résultat ; les anciennes mesures de chevauchement seules ne détectaient pas le défaut.
- Contrôle positif : PRE25, surface arrière mesurée à 1,35 pixel sur les mêmes mèches ; profondeur minimale requise 1,15 pixel. Six contrôles, aperçus avec bras larges/fins et aperçu dans le monde.
- Catalogue : 91 configurations, 273 poses ; les deux premières coiffures longues avec toutes les tenues, toutes les coiffures et barbes découvertes. Aucun chevauchement coplanaire ni croisement détecté.
- Joueur réel : 12 configurations et 60 poses, animation PAL active, races et nouvelles fixtures. Aucun maillage manquant ni chevauchement détecté. 48 contrôles des bras à la première personne : grille des vêtements préservée.
- Menu : 12 contrôles géométriques et 24 captures, trois angles distincts ; aucun chevauchement détecté.
- Contrôles indépendants : profondeur des mèches, distinction des détails, adaptation au volume natif, grille 1×1 et trous conservés. Le contrôle des raccords des vêtements passe aussi.
- 4 958 assets identiques à ceux de la PRE24, déjà comparés octet pour octet à la PRE17.

Minecraft 1.21.11, Java 21, Fabric, Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, EntityCulling 1.10.2, PAL 1.1.10 et HUD Capitale 1.3.2. Le rendu OpenGL utilise OSMesa, pas le GPU Windows de l'utilisateur. Le skin de compte est testé avec une texture de compte mise en cache, sans authentification Mojang en direct.

JAR : NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE25.jar

SHA-256 : d22c2af2b91133a2fb7215b8fc1df3e4f6954bfc89cc57e9bee68ca0245af419

Les captures de profil et de dos sont examinées après les essais. Les logs et les empreintes des exécutions sont archivés. Ces essais valident les cas décrits, sans remplacer un rendu dans chaque combinaison de shaders et de mods.
