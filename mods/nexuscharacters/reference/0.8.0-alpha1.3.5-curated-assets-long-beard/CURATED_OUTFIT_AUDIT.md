# Audit des tenues — alpha1.3.5

Audit effectué sur le JAR runtime `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.5-CURATED-ASSETS-LONG-BEARD-PERF1.jar`.

## Résultat

- 38 textures runtime dans `assets/nexuscharacters/appearance_parts_v064/outfits/`.
- Aucun PNG de tenue legacy/source (`clothes*.png`, `dress*.png`, `kingclothes.png`, ancien `outfit_*.png`) ne subsiste ailleurs dans le JAR.
- Toutes les tenues runtime proviennent du lot utilisateur `clothes.zip` ou d'une transformation/remap explicite de ce lot.
- `kingclothes.png` n'est plus distribué.
- Le doublon exact `armure3.png == côte de maille1.png` est supprimé : un seul visuel est conservé sous la Côte de maille 1.
- Le slot libéré est réutilisé pour un nouvel asset ; aucun trou n'est introduit dans les IDs.
- Audit SHA-256 de toutes les 38 textures runtime : aucun doublon binaire exact.

Conclusion : aucune ancienne tenue absente de `clothes.zip` n'est encore embarquée comme tenue sélectionnable, et aucun doublon exact ne subsiste dans les 38 slots.
