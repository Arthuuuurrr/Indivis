# Validation alpha1.1

Minecraft 1.21.11 — Fabric Loader >= 0.19.5.

## Bug corrigé

La version alpha1 rejetait certains marqueurs de skin/preset Haute Capitale de plus de 64 caractères via `skinUsername`, ce qui pouvait laisser le client sur l'écran de synchronisation après déconnexion serveur.

alpha1.1 :
- porte la limite à 256 caractères ;
- conserve le serveur comme autorité canonique ;
- améliore le motif de rejet ;
- ajoute un fail-safe de 20 secondes sur l'écran de chargement.

## Tests capturés dans le bundle source

- authority harness : 15 PASS ;
- structural verifier 0.8 : 26 PASS ;
- alpha1.1 verifier : 7 PASS ;
- regression verifier 0.7.8 : 19 PASS ;
- ASM structural verification : PASS, 160 classes ;
- ZIP/JAR integrity : PASS.

## Limite

Ces validations sont principalement structurelles/harness. Elles ne remplacent pas les tests runtime de l'issue #58 sur le modpack serveur réel.
