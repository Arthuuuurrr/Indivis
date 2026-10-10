# Nexus PRE33 et HUD 1.3.3 OPT1

Optimisation des préparations exactes des surfaces de personnage et des recherches réflexives. Base frozen PRE32 commit e6e01dfac5bf08cef696f2c50400dbb2fd849a0e. Issue #163. Branche empilée sur fix/nexus-pre32-arm-cache.

Lire PRE33-verifications.md et les données qa pour les résultats et leurs limites.

## Reconstruction des binaires

Avec Python 3 et les JAR originaux portant les empreintes des manifests :

    python assemble.py nexus /chemin/NexusCharacters-PRE32.jar /sortie/NexusCharacters-PRE33.jar
    python assemble.py hud /chemin/capitale_rp_hud_BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT.jar /sortie/capitale_rp_hud_OPT1.jar

L'assemblage vérifie les bases, chaque classe du patch et le SHA du JAR complet, puis remplace atomiquement la sortie.

## Compilation et banc

Le banc local a une arborescence pre33 pour ce dossier et pre31 pour le JDK/runtime Minecraft reconstruits. Les scripts de préparation du runtime sont dans la référence PRE31 du dépôt. Placer la base PRE32 dans pre33/base/NexusCharacters-PRE32.jar et le HUD original sous pre33/hud-base. Les versions des mods de test sont listées dans qa/test-mods.json.

    python pre33/build.py
    python pre33/run_client.py --baseline --diagnostics
    python pre33/run_client.py --compat --diagnostics
    python pre33/run_client.py --baseline --pose-only --compat --diagnostics
    python pre33/run_client.py --pose-only --compat --diagnostics
    python pre33/run_client.py --baseline --world-diag --compat
    python pre33/run_client.py --world-diag --compat
    python pre33/run_client.py --future-assets --compat
    python pre33/run_client.py --reflection-only --compat

Ne pas lancer deux clients avec le même numéro de PRE33_DISPLAY. Le runner conserve un état et un classpath de banc par contexte, exige un fichier de fin récent et échoue sur les marqueurs de régression.

Les composants de calcul géométrique originaux restent dans la base. Patch33.java ajoute les points de cache et délègue deux résolutions de champs et une résolution de méthode ; Verify33.java reconstruit les instructions avant points de cache et vérifie toutes les autres méthodes des classes touchées.
