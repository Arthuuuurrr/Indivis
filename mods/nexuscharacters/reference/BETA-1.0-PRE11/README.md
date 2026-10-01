# NexusCharacters — BETA 1.0 PRE11

Base exacte : BETA 1.0 PRE10.

PRE10 créait bien un Mesh via l'API de 3D Skin Layers, mais tentait de l'envoyer directement à la file de rendu 1.21.11. Cette file attend un ModelPart Minecraft, pas un Mesh Skin Layers. Le rendu 3D échouait donc alors que la texture de coiffure avait déjà été réduite à une version tête seulement.

PRE11 corrige cela :
- les coiffures longues utilisent de nouveau en permanence leur texture complète PRE9 comme fallback ;
- le mesh 3D est injecté dans un ModelPart vide via ModelPartInjector ;
- c'est ce ModelPart qui est soumis à la file de rendu Minecraft ;
- le mesh suit le transform du corps et utilise l'OffsetProvider.BODY de Skin Layers ;
- la légère séparation par rapport au vêtement est conservée ;
- la couleur reste liée à la couleur de cheveux Nexus.

Par rapport à PRE10, seules LongHair3DRenderSupport.class et fabric.mod.json doivent changer. Les correctifs PRE8/PRE9, les barbes, le layout, la sauvegarde et les autres cosmétiques restent inchangés.
