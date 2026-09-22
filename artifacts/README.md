# Artefacts binaires

Les JAR/ZIP compilés ne doivent pas devenir la seule source de vérité du projet.

server-manifest.yml enregistre :
- le nom exact ;
- la version ;
- le SHA-256 ;
- le statut stable/testing ;
- l'issue de validation associée.

À terme, les builds distribuables devront être produits par GitHub Actions et attachés à des Releases/artefacts de workflow.

## Important

Le bundle HAUTE_CAPITALE_FINAL_MODS_RC7B_HAZENN_CRASHFIX.zip contient un SHA256SUMS.txt devenu obsolète pour **Spell Power** après le hotfix RC7 Direct Resist. Le manifest Git utilise le hash recalculé sur le JAR réellement présent dans le ZIP.
