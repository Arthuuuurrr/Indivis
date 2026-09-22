# Mods

Ce dossier contient les sources complètes lorsqu'elles sont disponibles, ou une référence source explicite lorsqu'un module n'existe actuellement que sous forme de build historique.

## Règle

Un JAR seul n'est jamais considéré comme la source de vérité du code. Tant que sa source n'est pas importée, son nom et son SHA-256 sont enregistrés dans [server-manifest.yml](../server-manifest.yml).

## État actuel

- `nexuscharacters/` : sources de patch/autorité 0.8.0-alpha1.1 en cours d'import ;
- HUD, skills-items et creatures-bundle : builds canoniques référencés par hash ;
- stack spells/Arsenal/Hazenn : builds de test RC7 référencés par hash ;
- Heraldry, MMO Music Zones et dialogues/caméra : source/artefact courant encore à récupérer.

Voir [docs/MODULES.md](../docs/MODULES.md) pour le registre complet.

Chaque mod doit à terme avoir son propre sous-dossier avec :
- README ;
- fichiers Gradle ;
- code source ;
- compatibilité Minecraft/Fabric ;
- instructions de build ;
- dépendances ;
- tests utiles.
