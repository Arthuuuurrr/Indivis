# NexusCharacters — BETA 1.0 PRE13

Base exacte : BETA 1.0 PRE12.

PRE12 laissait les cheveux dans les UVs vanilla du jacket. 3D Skin Layers
pouvait donc les traiter seulement comme une partie du même mesh que la tenue,
sans séparation géométrique propre.

PRE13 crée une passe 3D hair-only dédiée via le MeshHelper public de
3D Skin Layers :
- styles 1..13 : mesh HEAD dédié ;
- styles longs 01..05 : mesh BODY dédié ;
- le mesh BODY est séparé davantage de la tenue (X 1.08 / Y 1.01 / Z 1.24) ;
- les pixels de cheveux de la surcouche vanilla sont retirés du skin composé
  quand Skin Layers est présent, pour éviter le double rendu et le z-fighting ;
- sans Skin Layers, les assets PRE12 complets restent le fallback.

Le hook existant de CharacterCosmeticFeatureRenderer couvre l'aperçu de
création et le rendu en jeu.
