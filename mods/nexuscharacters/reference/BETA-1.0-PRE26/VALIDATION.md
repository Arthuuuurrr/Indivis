# PRE26 — raccord des UV des bras

Le corps 2 et plusieurs tenues sources utilisent la disposition UV slim (bras de 3 pixels), tandis que les skins composés par Nexus déclarent un modèle classique (bras de 4 pixels). Ce mélange décalait les faces des manches et laissait des bandes de peau ou des trous. La PRE25 reproduit les cas signalés sur les captures ; la PRE26 adapte chaque source avant sa coloration, sa composition et sa promotion en couche 3D.

## Correction ciblée

- Conversion face par face des quatre zones des bras : gauche/droite, couche inférieure/supérieure. Les faces latérales de profondeur 4 restent à leur taille ; les faces de largeur 3 sont adaptées à 4 par duplication du pixel central. Les zones du torse, tête et jambes restent identiques.
- Corps 2 reconnu par ses UV ; corps 1, 3 et 4 conservés.
- Sources slim des tenues livrées reconnues individuellement, y compris les éléments clairsemés. Les nouveaux assets peuvent déclarer leur gabarit dans un fichier `NOM.arm-layout.json` contenant `{"arm_width":3}` ou `{"arm_width":4}`. Déclaration prioritaire sur la détection et les références d'origine.
- Aucun PNG livré modifié : 4 958 assets identiques octet pour octet à la PRE25.
- Classes de construction des cheveux, vêtements, découpage et proportions identiques à la PRE25. Seul le chargement des sources corps/tenues est adapté. Le skin Mojang conserve son propre modèle.

## Vérification du JAR livré

- Oracle indépendant : 896 pixels de faces étiquetés vérifient orientation, couleur, colonne et rangée. Conservation de tous les pixels hors bras, source immuable, identité des corps classiques.
- Catalogue : 364 configurations, les quatre corps, toutes les tenues découvertes avec deux coiffures longues, toutes les coiffures et barbes, avec proportions naines. 1 092 poses ; aucun chevauchement coplanaire ou croisement détecté. 364 vérifications de 448 pixels opaques sur les faces des bras.
- Joueur réel : 16 configurations, 80 poses, animation PAL active ; aucun maillage manquant ni chevauchement. 64 contrôles des grilles des bras en première personne ; rendu en première personne vérifié.
- Menu : 8 configurations, 24 contrôles géométriques des modèles larges/fins et de l'aperçu monde, 48 captures. Les cas ajoutés reproduisent corps 2/tenue 17 et corps 3/tenues 4, 6 et 11. Inspection des captures de face oblique, profil et dos.
- Contrôle négatif : exactement le même contrôle des bras rejette le JAR PRE25 deux fois pour le corps 2, coordonnées x=54/y=20. Les captures PRE25 montrent les manches décalées sur les autres tenues ; comparaison visuelle archivée avec les captures PRE26.
- Préservation de la correction cheveux : six contrôles de profondeur, contrôle de 41 échantillons du raccord arrière. Tests purs HairProfileRegression, ClothingProfileRegression, SurfaceGeometryRegression, SurfaceSeamRegression et PoseAuditRegression passent.
- Trois ressources nouvelles externes testent un corps slim, une tenue slim clairsemée et une tenue classique, avec leurs déclarations de gabarit. Les cinq fixtures de catalogue précédentes sont également chargées. Ces ressources ne sont pas ajoutées au JAR livré.
- Réassemblage depuis PRE25 et classes compilées : résultat strictement identique au JAR testé, SHA-256 ci-dessous.

Minecraft 1.21.11, Java 21, Fabric API 0.141.6, Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Player Animation Library 1.1.10. Rendu OpenGL OSMesa ; pas un essai sur le GPU Windows de l'utilisateur. Test du skin de compte avec une texture en cache, sans authentification Mojang en direct. Aucune mesure FPS utilisateur.

Version : 1.0.0-beta.26+hc.1.21.11

SHA-256 : ea070e3920cd7c74926115886949b911bd887ed408eac975524ba667f941496c
