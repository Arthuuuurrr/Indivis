# NexusCharacters Haute Capitale — 0.8.0-alpha1.1

Référence importée depuis le bundle :

`NexusCharacters-HauteCapitale-v0.8.0-alpha1.1-LEGACY-SKIN-COMPAT-SOURCES-AND-TESTS.zip`

SHA-256 du bundle source/tests :

`afbf2f4aa51bd1597d2230d85e6efaf9dbe46c50629a01d259c51bc72e1c3cb4`

Build runtime correspondant :

`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.1-LEGACY-SKIN-COMPAT.jar`

SHA-256 :

`8d918571c3f7ff518c94c0793fb1080a0f08cc2d9e697d6a0584d09a551efa1e`

## Nature de cette source

Le bundle récupéré est un **bundle de patch/source/tests**, pas le workspace Gradle complet de NexusCharacters. Les fichiers importés ici documentent et reproduisent les modifications Haute Capitale autour de l'autorité serveur et du correctif legacy-skin.

Les deux sources principales sont :
- `sources/ServerAuthorityV080.java`
- `sources/Patch080A11.java`

## Validation encore nécessaire

Voir issue #58 : autorité serveur, isolation entre personnages, crash/déconnexion, reconnexion et tests multijoueur doivent être validés en runtime sur Nitroserv.
